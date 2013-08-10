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

		if(_interface.constructor){
			var ret = function(opt){
				return _pack(_interface.constructor(
					typeof opt === 'object' ? JSON.stringify(opt) : opt)
				);
			};
			ret.toString = function(){return 'function(){[native code]}'};			
		}else{
			ret = {};
		}

		var reflects = JSON.parse(_interface.__reflects__());

		reflects.forEach(function(meta){
			var key = meta.apiName,
				method = _interface[key];

			//is api returns join?
			var isRetJSON = (meta.apiRet == meta.RET_JSON);

			var func = (function(fn, isJSON){
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
					
					var ret = fn.apply(_interface, args);
					if(isJSON){
						return JSON.parse(ret);
					}else{
						return ret;
					}						
				}
				_f.toString = function(){return 'function(){[native code]}'};
				return _f;
			})(method, isRetJSON);

			if(meta.apiType == meta.TYPE_GETTER){
				(function(propertyName, func){
					ret.__defineGetter__(propertyName, function(){
						return func();
					});
				})(key, func);				
			}else if(meta.apiType == meta.TYPE_SETTER){
				(function(propertyName, func){
					ret.__defineSetter__(propertyName, function(v){
						return func(v);
					});		
				})(key, func);					
			}else{
				ret[key] = func;
			}
		});
		
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

	function mix(des, src, override) {
		if (typeof override == 'function') {
			for (i in src) {
				des[i] = override(des[i], src[i], i);
			}
		}
		else {
			for (i in src) {
				if (override || !(des[i] || (i in des))) { 
					des[i] = src[i];
				}
			}
		}
		return des;
	}

	novaRoot.nova.utils = {};
	
	mix(novaRoot.nova.utils, {
		mix: mix,
		dateFormat: function(d, pattern) {
			d = d || new Date();
			if(!(d instanceof Date)){
				d = new Date(d);
			}
			pattern = pattern || 'yyyy-MM-dd';
			var y = d.getFullYear().toString(),
				o = {
					M: d.getMonth() + 1, //month
					d: d.getDate(), //day
					h: d.getHours(), //hour
					m: d.getMinutes(), //minute
					s: d.getSeconds() //second
				};
			pattern = pattern.replace(/(y+)/ig, function(a, b) {
				return y.substr(4 - Math.min(4, b.length));
			});
			for (var i in o) {
				pattern = pattern.replace(new RegExp('(' + i + '+)', 'g'), function(a, b) {
					return (o[i] < 10 && b.length > 1) ? '0' + o[i] : o[i];
				});
			}
			return pattern;
		},
	});

	this.$get = function(){
		return novaRoot.nova;
	}
});