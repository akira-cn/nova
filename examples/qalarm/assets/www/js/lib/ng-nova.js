angular.module('weizoo.nova', [])

.provider('$nova', function(){

	//the callbacks related by _uuid
	var _callbacks = [];

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

		var reflects = JSON.parse(_interface.__exports__());

		reflects.forEach(function(meta){

			if(typeof meta == 'string'){
				meta = {
					apiName		: meta,
					apiType		: 0,
					apiRet		: 0,

					TYPE_METHOD	: 0,
					TYPE_GETTER : 1,
					TYPE_GETTER : 2,

					RET_VALUE	: 0,
					RET_JSON	: 1,
					RET_FUNC	: 2,
				}
			}

			var key = meta.apiName,
				method = _interface[key];

			var func = (function(fn, meta){
				var _f = function(){
					var args = [].slice.call(arguments);

					for(var i = 0; i < args.length; i++){
						var arg = args[i];
						if(typeof arg == 'function'){ 
							//save callbacks func
							_callbacks.push({thisObj: _interface, fn: arg});
							args[i] = _callbacks.length - 1;
						}else if(typeof arg == 'object'){
							args[i] = JSON.stringify(arg);
						}else{
							args[i] = arg;
						}
					}
					
					var ret = fn.apply(_interface, args);
					if(meta.apiRet == meta.RET_JSON){
						return JSON.parse(ret);
					}else if(meta.apiRet == meta.RET_FUNC){
						return _callbacks[ret].fn;
					}else{
						return ret;
					}						
				}
				_f.toString = function(){return 'function(){[native code]}'};
				return _f;
			})(method, meta);

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
			_callbacks[data.id] = null;
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

	if(novaRoot.nova){
		
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
	}

	this.$get = function(){
		return novaRoot.nova;
	}
});