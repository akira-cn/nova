angular.module('weizoo.nova', [])

.provider('$nova', function(){
	function _uuid(){
		function uuid_part(length){
			var uuidpart = "";
			for(var i = 0; i < length; i++){
				var uuidchar = parseInt((Math.random()*256)).toString(16);
				if(uuidchar.length == 1){
					uuidchar = "0" + uuidchar;
				}
				uuidpart += uuidchar;
			}
			return uuidpart;
		}

		return [uuid_part(4), uuid_part(2), uuid_part(2), uuid_part(2), uuid_part(6)].join('-');
	}

	//the callbacks related by _uuid
	var _callbacks = {};

	function _pack(_interface){
		//fix the bug in early version
		var keys = (_interface.keys()+"").split(','); 

		if(keys.indexOf('constructor') >= 0){
			var ret = function(opt){
				return _pack(_interface.constructor(
					typeof opt === 'object' ? JSON.stringify(opt) : opt)
				);
			}
			ret.toString = function(){return 'function(){[native code]}'};
		}else{
			var ret = {};
		}
		for(var j = 0; j < keys.length; j++){
			var key = keys[j];
			var method = _interface[key];
			if(typeof method == 'function'){
				if(key == 'constructor'){
					continue;
				}
				var func = (function(fn){
					var _f = function(){
						var args = [].slice.call(arguments);

						for(var i = 0; i < args.length; i++){
							var arg = args[i];
							if(typeof arg == 'function'){ 
								//find callbacks func by uuid
								var uuid = _uuid(arg);
								_callbacks[uuid] = {thisObj: _interface, fn: arg};
								args[i] = uuid;
							}else if(typeof arg == 'object'){
								args[i] = JSON.stringify(arg);
							}else{
								args[i] = arg;
							}
						}

						return fn.apply(_interface, args);
					}
					_f.toString = function(){return 'function(){[native code]}'};
					return _f;
				})(method);
				if(/^__getter__/.test(key)){
					//getter from java
					var propertyName = key.replace(/^__getter__/, '');

					(function(propertyName, func){
						ret.__defineGetter__(propertyName, function(){
							return func();
						});
					})(propertyName, func);

				}else if(/^__setter__/.test(key)){
					var propertyName = key.replace(/^__setter__/, '');
					
					(function(propertyName, func){
						ret.__defineSetter__(propertyName, function(v){
							return func(v);
						});		
					})(propertyName, func);		

				}else{
					ret[key] = func;
				}
			}else{
				ret[key] = method;
			}
		}
		return ret;		
	}

	var novaRoot = {};

	var nova = {
		
		_init : function(){
			//unpack module
			//window['.a.b'] -> window.a.b

			for(var module in window){
				
				if(/^\.nova/.test(module)){ 
					var _interface = window[module];

					var _paths = module.split('.');
					var _root = novaRoot;

					for(var i = 1; i < _paths.length - 1; i++){
						var _path = _paths[i];
						_root[_path] = _root[_path] || {};
						_root = _root[_path];
					}

					_root[_paths[i]] = _pack(_interface);
					delete window[module];
				}
			}
		}
	};

	window.addEventListener('novaCallback', function(evt){
		try{
			var data = JSON.parse(evt.data+'');
			var callback = _callbacks[data.id],
				result = data.result;
			callback.fn.call(callback.thisObj, result);
		}finally{
			//delete _callbacks[data.id];
		}
	});

	window.addEventListener('novaCallbackEvent', function(evt){
		try{
			var data = JSON.parse(evt.data+'');
			var callback = _callbacks[data.id],
				ev = {
					data : data.data,
					type : data.type
				};
			//console.log(evt.data);
			callback.fn.call(callback.thisObj, ev);
		}finally{
			//delete _callbacks[data.id];
		}
	});

	nova._init();

	this.$get = function(){
		return novaRoot.nova;
	}
});