$.ajaxSendPost = function (){
	var token = $("meta[name=_csrf]").attr("content");
	var header = $("meta[name=_csrf_header]").attr("content");
	$(document).ajaxSend(function(e, xhr, options){
		xhr.setRequestHeader(header, token);
	});
};
$.ajaxSendPost();

$.getDataByAjax = function (options){
	var obj;
	$.ajax({
		type: "POST",
		url: options.url,
		data: options.data,
		async: false,
		success: function (data){
			obj = data;
		}
	});
	return obj;
};
$.postAjax = function (options){
	if (options.url){
		if (options.before && typeof options.before == "function"){
			options.before();
		}
		
		$.ajax({
			type: "POST",
			url: options.url,
			data: options.data,
			success: function (data){
				if (data != "") {
					var layers = false;
					if (data.state){
						if (options.success &&options.callback && typeof options.callback == "function"){
							layers = options.callback(data);
						}
						if (!layers)
							layer.msg(data.msg,{icon:1,time: 1500});
						
						if (options.flash)
							window.location.reload();
					}else{
						if (options.error &&options.callback && typeof options.callback == "function"){
							layers = options.callback();
						}
						if (!layers)
							layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> "+data.msg,{
								time: 1500,
								skin: "layui-layer-error"
							});
					}
				}
			},
			error: function (){
				if (options.error &&options.callback && typeof options.callback == "function"){
					options.callback();
				}
				layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> \u7CFB\u7EDF\u5F02\u5E38\uFF01",{
					time: 1500,
					skin: "layui-layer-exception"
				});
			}
		});
		
	}
	
};
$.postAjaxByJson = function (options){
	if (options.url){
		if (options.before && typeof options.before == "function"){
			options.before();
		}
		
		$.ajax({
			type: "POST",
			url: options.url,
			data: options.data,
			contentType : 'application/json;charset=utf-8',
			success: function (data){
				if (data.state){
					if (options.success && options.callback && typeof options.callback == "function"){
						options.callback();
					}
					layer.msg(data.msg,{
						icon: 1,time: 1000
					});
				}else{
					if (options.error && options.callback && typeof options.callback == "function"){
						options.callback();
					}
					layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> "+data.msg,{
						skin: "layui-layer-error",time: 1500
					});
				}
			},
			error: function (){
				if (options.error && options.callback && typeof options.callback == "function"){
					options.callback();
				}
				layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> \u7CFB\u7EDF\u5F02\u5E38\uFF01",{
					skin: "layui-layer-exception",time: 1500
				});
			}
		});
		
	}
	
};

$.fn.serializeData = function(){
	var data = {};
	var serializeData = $(this).serializeArray();
	for (var index in serializeData) {
		data[serializeData[index].name] = serializeData[index].value;
	}
	return data;
};

$.toThousands = function (num) {
	if (isNaN(num)) {return num;}
	num = Number(num).toFixed(2);
    var num = (num || 0).toString(), result = '';
    var pointIndex = num.indexOf(".");
    var decimal = "";
    if (pointIndex >= 0){
    	decimal = num.slice(pointIndex, num.length);
    	num = num.slice(0, pointIndex);
    }
    while (num.length > 3) {
        result = ',' + num.slice(-3) + result;
        num = num.slice(0, num.length - 3);
    }
    if (num) { result = num + result + decimal; }
    return result;
};

$.stringEllipsis = function (str, start, end) {
	var length = Number(start) + Number(end) + 3,result = "";
	var size = str.length;
	if (size <= length) {return str;}
	result = str.slice(0, start) + "..." + str.slice(size-end, size);
	return result;
};

$.bankState = function (){
	$(".on-off").on("click",function(){
		var _this = $(this);
		var _url = _this.attr("data-ajax-url");
		if (_url){
			var _id = _this.attr("id");
			var checked = _this.attr("data-checked")=="true"?true:false;
			_this.attr("disabled", true);
			$.ajax({
				type: "POST",
				url: "product/state",
				data: {id: _id, checked: checked},
				success: function (data){
					_this.attr("disabled", false);
					if (data != "") {
						layer.msg(data.msg,{icon:1,time: 1500,offset: "150px"});
						_this.attr("data-checked", !checked).attr("data-original-title",checked?"禁用":"启用");
						_this.find("i.fa").attr("class",checked?'fa fa-ban':'fa fa-check');
						_this.attr("class",checked?'orange':'green');
						_this.parents("li").find("img").attr("class",checked?'':'disabled');
					}else{
						layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> "+data.msg,{
							time: 1500,
							offset: "150px",
							skin: "layui-layer-error"
						});
					}
				},
				error: function (){
					_this.attr("disabled", false);
					layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> \u7CFB\u7EDF\u5F02\u5E38\uFF01",{
						time: 1500,
						offset: "150px",
						skin: "layui-layer-exception"
					});
				}
			});
		}
	});
};

$.fn.rate = function (){
	this.on("keyup blur", function (){
		var _value = this.value;
		this.value = _value.replace(/[^0-9.]/g,"");
		_value = this.value;
		if (_value.indexOf("..")>=0) {
			this.value = _value.replace(/[.]/g,"");
			_value = this.value;
		}
		var index1 = _value.indexOf(".");
		var index2 = _value.lastIndexOf(".");
		if (index1!=index2&&index2>0) {
			var arr = _value.split("");
			arr.splice(index2,1);
			this.value = arr.join("");
		}
		var first = _value.charAt(0);
		var reg = /[0-9]/;
		if (!reg.test(first)) {
			this.value = _value.replace(first,"");
		}
		if (reg.test(this.value)) {
			if (Number(this.value)>100) {
				this.value = 0;
			}
		}
	});
};

$.page_init = function (){
	$('[data-rel=tooltip]').tooltip();
	$("[data-rel*=popover]").popover({container:'body'});
	
	$(".on-or-off").off().on("click",function(){
		var _url = this.getAttribute("data-ajax-url");
		if (_url){
			var _id = this.value;
			var checked = this.checked;
			$.postAjax({
				error: true,
				success: false,
				data: {id: _id, checked: checked},
				url: _url
			});
		}
	});
	
	$(".btn-delete").on("click",function(e){
		e.stopPropagation();
		var _url = this.getAttribute("data-ajax-url");
		var _flash = this.getAttribute("data-flash");
		var _toggle = this.getAttribute("data-toggle");
		if (_url){
			var _id = this.id;
			layer.confirm('\u662F\u5426\u786E\u5B9A\u5220\u9664\uFF1F', {
				title: "<li class='fa fa-question-circle-o bigger-120 red'></li> \u63D0\u793A",
				btn: ['\u786E\u5B9A', '\u53D6\u6D88'] //\u6309\u94AE
			}, function(){
				$.postAjax({
					error: true,
					success: true,
					flash: _flash,
					data: {id: _id},
					url: _url,
					before: function (){
						$(".btn-delete").attr("disabled", true);
					},
					callback: function (){
						$(".btn-delete").attr("disabled", false);
						if (_toggle)
							$("a#"+_id).parents("li").animate({width: "toggle"},300);
						
						if ($("#sample-table").length > 0)
							$("#sample-table").dataTable().fnDraw(false);
						
					}
				});
			});
		}
	});
	
	$(".info-save").off().on("click", function (){
		var nodraw = this.getAttribute("data-nodraw");
		var arr = $("#info-form .required");
		if (arr.length > 0) {
			for (var i = 0; i < arr.length; i++) {
				var doc = $(arr[i]);
				var _value = doc.val();
				if (_value == "") {
					doc.focus();
					doc.next("li.i-prt").css("display", "block");
					layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> 不能为空！",{time: 1500,skin: "layui-layer-error",offset: "150px"});
					return;
				}else{
					doc.next("li.i-prt").css("display", "none");
				}
			}
		}
		$.postAjax({
			error: true,
			success: true,
			data: $.serializeValueNotNull("#info-form"),
			url: this.getAttribute("data-ajax-url"),
			before: function (){
				$(".info-save").attr("disabled", true);
				$(".widget-box-overlay").removeClass("none");
			},
			callback: function (){
				$(".info-save").attr("disabled", false);
				$(".widget-box-overlay").addClass("none");
				if (!nodraw) {
					if ($("#sample-table").length > 0){
						$("#sample-table").dataTable().fnDraw(false);
					}
				}
			}
		});
	});
	
	$(".user-group").on("click", function (){
		var _value = this.getAttribute("data-parent-value");
		if (_value) {
			var roleName = this.getAttribute("data-role-name");
			var child = $(".panel-group").find("input[value="+ _value +"]:checked");
			$(".coll_parent").removeClass("none").addClass("none");
			for (var i = 0; i < child.length; i++) {
				var parent_id = $(child[i]).attr("data-parent-id");
				$(parent_id).removeClass("none");
			}
			layer.msg("\u5DF2\u5207\u6362\u81F3\u2018"+roleName+"\u2019\u5217\u8868\uFF01",{time: 1000});
		}else{
			$(".coll_parent").removeClass("none");
			layer.msg("\u5DF2\u5207\u6362\u81F3\u2018\u5168\u90E8\u7BA1\u7406\u5458\u2019\uFF01",{time: 1000});
		}
	});
	
	$("input[data-parent-id]").on("click", function (){
		var userId = this.getAttribute("data-user-id");
		var roleId = this.value;
		var _this = this;
		$.postAjax({
			success: true,
			data: {id: userId,roleId: roleId,type: this.checked},
			url: "user/roles",
			callback: function (){
				var userName = $("label[data-user-id="+userId+"]").text();
				var roleName = _this.getAttribute("data-role-name");
				var msg = "\u6210\u529F\uFF01\u8D4B\u4E88\u7528\u6237\uFF1A";
				if (!_this.checked) {
					msg = "\u5DF2\u64A4\u9500\u7528\u6237\uFF1A";
				}
				layer.msg(msg+userName+"\u201C"+roleName+"\u201D\u8EAB\u4EFD\u3002",{time: 1500});
				return true;
			}
		});
	});
	
	$("#permissions input:checkbox").off().on("click", function (){
		$.findParentPermisson($(this), this.checked, $(this));
	});
	
	$.findParentPermisson = function (doc, checked, fDoc){
		var end = doc.attr("data-resource-end")=="true";
		var child = 0;
		if (fDoc != null) {
			child = $("#permissions input[parent-id="+fDoc.val()+"]").length;
			if (child > 0) {
				var _id = doc.attr("data-per");
				$(_id).find("input[type=checkbox]").prop("checked", checked);
			}
		}
		var _parent_id = doc.attr("parent-id");
		if (_parent_id) {
			var _parent = $("#permissions input[value="+_parent_id+"]:checkbox");
			if (_parent.length > 0) {
				var _child = $("#permissions input[parent-id="+_parent_id+"]:checked");
				if (_child.length > 0 || end) {
					_parent.prop("checked",true);
				}else{
					_parent.prop("checked",false);
				}
				$.findParentPermisson(_parent, checked, null);
			}
		}
	};
	
	$(".btn-pass").each(function (){
		$(this).ModalBox({
			title: "\u4FEE\u6539\u5BC6\u7801",
			url: "user/pass",
			hideData: [{name: "id",value: $(this).attr("id")}],
			forms: [
				{title: "\u767B\u5F55\u5BC6\u7801",name: "passWord",doc: "input",docType: "password"},
				{title: "\u786E\u8BA4\u5BC6\u7801",name: "confirmPass",doc: "input",docType: "password"}
			]
		});
	});
};
$.productsEdit = function (products,parentId){
	for (var i = 0; i < products.length; i++) {
		var data = products[i];
		var id = data.id
		var mark = data.productMark;
		$(".product-edit#"+mark).ModalBox({
			title: "\u7F16\u8F91\u4EA7\u54C1",
			url: "product/edit",
			_flash: true,
			hideData: [{name: "id",value: data.id},{name: "parentId",value: parentId}],
			forms: [
				{title: "\u4EA7\u54C1\u6807\u8BC6",name: "productMark",doc: "input",
				docType: "text",readonly: true,nullable: true,value: mark},
				{title: "\u4EA7\u54C1\u540D\u79F0",name: "productName",doc: "input",docType: "text",value: data.productName},
				{title: "\u4EA7\u54C1\u7C7B\u578B",name: "type",doc: "select",callback: function (){
					return "<option value=''>--\u8BF7\u9009\u62E9--</option>" + 
							"<option value='0' "+(data.type==0?"selected":"")+">\u666E\u901A\u4EA7\u54C1</option>" + 
							"<option value='1' "+(data.type==1?"selected":"")+">\u7F51\u94F6\u4EA7\u54C1</option>";
				}},
				{title: "\u4EA7\u54C1\u63CF\u8FF0",name: "desc",doc: "textarea",nullable: true,value: data.desc}
			]
		});
		$("select#"+mark).defaultGallery(id);
	}
};
$.fn.defaultGallery = function (id){
	var _this = $(this);
	_this.on("change", function (){
		var _value = _this.val();
		if (_value) {
			$.postAjax({
				success: true,
				error: true,
				data: {id: id,galleryId: _value},
				url: "product/default"
			});
		}
	});
};
$.serializeValueNotNull = function (element){
	var data = $(element).serialize();
	var array = data.split("&");
	for (var int = 0; int < array.length; int++) {
		var str = array[int];
		if (str.indexOf("=") == (str.length-1)) {
			array.splice(int, "1")
			int--;
		}
	}
	return array.join("&")
};

$.copyData = function () {
	var array = $(".clipboard");
	if (array.length > 0) {
		for (var i = 0; i < array.length; i++) {
			var copy = $(array[i]);
			copy.zclip({
				path: 'assets/js/zclip/ZeroClipboard.swf',
				copy: function (){
					return $(this).prev().text();
				},
				beforeCopy: function() {
					this.style.color = "orange";
				},
				afterCopy: function() {
					this.style.color = "#428bca";
					var $copysuc = $("<div class='copy-tips'><div class='copy-tips-wrap'>\u263A \u590D\u5236\u6210\u529F</div></div>");
                    $("body").find(".copy-tips").remove().end().append($copysuc);
                    $(".copy-tips").fadeOut(1500);
				}
			});
		}
	}
};

$.getMerchantState = function (full){
	var obj = {};
	var arr = [];
	var state = full.state;
	switch (state) {
	case 1:
		obj = {
			title: "\u542F\u7528",
			_class: "green",
			icon: "fa-check-circle-o"
		};
		arr = [
			{
				title: "\u7981\u7528",
				_class: "red",
				icon: "fa-ban",
				data: {id: full.id, state: 2}
			},
			{
				title: "\u5173\u95ED\u652F\u4ED8",
				_class: "orange",
				icon: "fa-exclamation-triangle",
				data: {id: full.id, state: 3}
			}
		];
		break;
	case 2:
		obj = {
			title: "\u7981\u7528",
			_class: "red",
			icon: "fa-ban"
		};
		arr = [
			{
				title: "\u542F\u7528",
				_class: "green",
				icon: "fa-check-circle-o",
				data: {id: full.id, state: 1}
			},
			{
				title: "\u5173\u95ED\u652F\u4ED8",
				_class: "orange",
				icon: "fa-exclamation-triangle",
				data: {id: full.id, state: 3}
			}
		];
		break;
	case 3:
		obj = {
			title: "\u5173\u95ED",
			_class: "orange",
			icon: "fa-exclamation-triangle"
		};
		arr = [
			{
				title: "\u5F00\u542F\u652F\u4ED8",
				_class: "green",
				icon: "fa-check-circle-o",
				data: {id: full.id, state: 1}
			},
			{
				title: "\u7981\u7528",
				_class: "red",
				icon: "fa-ban",
				data: {id: full.id, state: 2}
			}
		];
		break;

	default:
		obj = {
			title: "\u672A\u6FC0\u6D3B",
			_class: "light-grey",
			icon: "fa-eye-slash"
		};
		arr = [
			{
				title: "\u6FC0\u6D3B",
				_class: "green",
				icon: "fa-check-circle-o",
				data: {id: full.id, state: 1}
			}
		];
		break;
	}
	var str = '<div class="inline position-relative">' +
				'<a href="javascript:;" class="user-title-label dropdown-toggle" data-toggle="dropdown">' + 
					'<span class="'+ obj._class +'"><i class="fa '+ obj.icon +'"></i> '+ obj.title +'</span>' + 
				'</a>' + 
			
				'<ul class="align-left dropdown-menu dropdown-caret dropdown-lighter">' + 
					'<li class="dropdown-header"> \u66F4\u6362\u72B6\u6001 </li>';
	for (var i = 0; i < arr.length; i++) {
		var _obj = arr[i]
		str += "<li data-ajax-data='"+ JSON.stringify(_obj.data) +"' data-ajax-url='merchant/state' class='merchant-state'><a href='javascript:;'>" + 
					'<span class="'+ _obj._class +'"><i class="fa '+ _obj.icon +'"></i> '+ _obj.title +'</span>' + 
				'</a></li>';
	}
	str += '</ul></div>';
	
	return str;
};
$.openInTheNewWindow = function (options){
	if (options.formData) {
		var tempForm = document.createElement("form");
		$.extend(tempForm,options.formData);
		
		var dataArr = options.dataArr;
		if (dataArr && dataArr.length > 0) {
			for (var i = 0; i < dataArr.length; i++) {
				var hideInput = document.createElement("input");
				hideInput.type = "hidden";
				$.extend(hideInput,dataArr[i]);
				tempForm.appendChild(hideInput);
			}
			
			if (window.addEventListener) {
				tempForm.addEventListener("onsubmit", function() {
					window.open('manage_blank', tempForm.target);
				});
			} else if (window.attachEvent) {
				tempForm.attachEvent("onsubmit", function() {
					window.open('manage_blank', tempForm.target);
				});
			}
			document.body.appendChild(tempForm);
			tempForm.submit();
			document.body.removeChild(tempForm);
		}
		
	}
};
$(function (){
	$.page_init();
	$(".invisible").remove();
//	$(document).on("keydown",function(e) {
//		// F12/Ctrl + Shift + I/Shift + F10
//		if (e.keyCode == 123 || ((e.shiftKey) && (e.keyCode == 121)) 
//				|| ((e.ctrlKey) && (e.shiftKey) && (e.keyCode == 73))) {
//			return false;
//		}
//	});
//	$(document).on("contextmenu",function(e) {
//		var _menu = $("#context-menu");
//		if (_menu.length > 0) {
//			var innerWidth = window.innerWidth;
//			var innerHeight = window.innerHeight;
//			var _x = e.clientX, _y = e.clientY;
//			var _width = parseInt(_menu.css("width"));
//			var _height = parseInt(_menu.css("height"));
//			var _left = _x;
//			if ((_x + _width) >= innerWidth)
//				_left = innerWidth - _width;
//			var _top = _y;
//			if ((_y + _height) >= innerHeight)
//				_top = innerHeight - _height;
//			_menu.css("left", _left + "px").css("top", _top + "px").removeClass("none");
//			$(document).on("click",function (){
//				_menu.addClass("none");
//			});
//		}else{
//			layer.msg('\u8BF7\u8054\u7CFB\u7BA1\u7406\u5458\uFF01',{anim: 6,time: 600});
//		}
//		return false;
//	});
	
//	$("#webSearch").on("click", function (){
//		layer.prompt({title: '\u8BF7\u8F93\u5165\u8BA2\u5355\u53F7'}, function(orderNumber, index){
//			window.location.href = "/order.html?"+orderNumber;
//		});
//	});
	
	$("#btn-logout").on("click", function (){
		layer.confirm('\u662F\u5426\u786E\u5B9A\u9000\u51FA\u767B\u5F55\uFF1F', {
			title: "<li class='fa fa-question-circle-o bigger-120 red'></li> \u63D0\u793A",
			btn: ['\u786E\u5B9A', '\u53D6\u6D88'] //\u6309\u94AE
		}, function(){
			document.getElementById('logout').submit()
		});
	});
	
	$(".pass-save").on("click", function (){
		var _pass = $("#password").val();
		if (_pass == ""){
			$("#password").focus();
			layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> " +
					"\u65B0\u5BC6\u7801\u4E0D\u80FD\u4E3A\u7A7A\uFF01",{
				skin: "layui-layer-error",time: 1500,offset: "150px"
			});
			return;
		}
		if (_pass.length < 6 || _pass.length > 16){
			$("#password").focus();
			layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> " +
					"\u5BC6\u7801\u957F\u5EA6\u4E0D\u7B26\uFF0C6-16\u4F4D\uFF01",{
				skin: "layui-layer-error",time: 1500,offset: "150px"
			});
			return;
		}
		var _confirm = $("#confirmPass").val();
		if (_confirm == ""){
			$("#confirmPass").focus();
			layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> " +
					"\u8BF7\u518D\u6B21\u8F93\u5165\u65B0\u5BC6\u7801\uFF01",{
				skin: "layui-layer-error",time: 1500,offset: "150px"
			});
			return;
		}
		if (_confirm != _pass){
			$("#confirmPass").focus();
			layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> " +
					"\u4E24\u6B21\u5BC6\u7801\u8F93\u5165\u4E0D\u4E00\u81F4\uFF01",{
				skin: "layui-layer-error",time: 1500,offset: "150px"
			});
			return;
		}
		$.postAjax({
			success:true,
			url: this.getAttribute("data-ajax-url"),
			data: $("#edit-password input").serialize(),
			before: function (){
				$(".pass-save").attr("disabled", true);
			},
			callback: function (){
				$("#password,#confirmPass").val("");
				$(".pass-save").attr("disabled", false);
			}
		});
	});
});

$.orderEcharts = function (data){
	require(
    	['echarts','echarts/theme/macarons','echarts/chart/line','echarts/chart/bar'],
    	function (ec,theme) {
    	var myChart = ec.init(document.getElementById('main'),theme);
    	option = {
        	tooltip : {trigger: 'axis'},
        	toolbox: {
	        	show : true,
	        	feature : {
		        	mark : {show: true},
		        	dataView : {show: true, readOnly: false},
		        	magicType: {show: true, type: ['line', 'bar']},
		        	restore : {show: true},
		        	saveAsImage : {show: true}
	        	}
        	},
        	calculable : true,
        	legend: {
        		data:['\u8BA2\u5355\u7B14\u6570','\u652F\u4ED8\u91D1\u989D','\u5546\u6237\u5229\u6DA6','\u5E73\u53F0\u5229\u6DA6']
        	},
        	xAxis : [
	        	{
	        		type : 'category',
	        		data : data.xAxisData
	        	}
        	],
        	yAxis : [
	        	{
		        	type : 'value',
		        	name : '\u8BA2\u5355',
		        	axisLabel : {
		        		formatter: '{value} \u5355'
		        	}
	        	},
	        	{
		        	type : 'value',
		        	name : '\u91D1\u989D',
		        	axisLabel : {
		        		formatter: '{value} \u5143'
		        	}
	        	}
        	],
        	series : [
	        	{
		        	name:'\u8BA2\u5355\u7B14\u6570',
		        	type:'line',
		        	yAxisIndex: 0,
		        	itemStyle: {normal: {label: {show: true}}},
		        	data:data.seriesDatas[3]
	        	},
	        	{
		        	name:'\u652F\u4ED8\u91D1\u989D',
		        	type:'bar',
		        	yAxisIndex: 1,
		        	data:data.seriesDatas[0]
	        	},
	        	{
		        	name:'\u5546\u6237\u5229\u6DA6',
		        	type:'bar',
		        	yAxisIndex: 1,
		        	data:data.seriesDatas[1]
	        	},
	        	{
		        	name:'\u5E73\u53F0\u5229\u6DA6',
		        	type:'bar',
		        	yAxisIndex: 1,
		        	data:data.seriesDatas[2]
	        	}
			]
		};
		myChart.setOption(option);
		window.addEventListener("resize", function() {
			myChart.resize();
		});
	});
};
$.pieEcharts = function (data){
	require(
			['echarts','echarts/theme/macarons','echarts/chart/pie','echarts/chart/funnel'],
			function (ec,theme) {
				var myChart = ec.init(document.getElementById('total-main'),theme);
				option = {
					title : {
				        text: '\u901A\u9053\u4EA4\u6613\u91D1\u989D',
				        subtext: '\u5229\u6DA6\u7EDF\u8BA1',
				        x:'center'
				    },
					tooltip : {trigger: 'item',formatter: "{a} <br/>{b} : {c} ({d}%)"},
					toolbox: {
						show : true,
						feature : {
							mark : {show: true},
							dataView : {show: true, readOnly: false},
							magicType: {
								show: true, 
								type: ['pie', 'funnel'],
								option: {
				                    funnel: {
				                        x: '25%',
				                        width: '50%',
				                        funnelAlign: 'left',
				                        max: data[0].totalAmount
				                    }
				                }
							},
							restore : {show: true},
				            saveAsImage : {show: true}
						}
					},
					calculable : true,
					legend: {
						orient : 'vertical',
				        x : 'left',
						data:['\u652F\u4ED8\u91D1\u989D','\u5546\u6237\u5229\u6DA6','\u5E73\u53F0\u5229\u6DA6']
					},
					series : [
						{
							name:'\u652F\u4ED8\u6210\u529F\u91D1\u989D/\u5229\u6DA6',
							type:'pie',
							radius : '55%',
				            center: ['50%', '60%'],
							data: [
								{name: '\u652F\u4ED8\u91D1\u989D',value: data[0].totalAmount},
								{name: '\u5546\u6237\u5229\u6DA6',value: data[0].totalMerchantProfits},
								{name: '\u5E73\u53F0\u5229\u6DA6',value: data[0].totalPlatformProfits}
							]
						}
					]
				};
				myChart.setOption(option);
				window.addEventListener("resize", function() {
					myChart.resize();
				});
			});
};
$.orderProductEcharts = function (data){
	require(
		['echarts','echarts/theme/macarons','echarts/chart/line','echarts/chart/bar'],
		function (ec,theme) {
			var myChart = ec.init(document.getElementById('product_main'),theme);
			option = {
				tooltip : {trigger: 'axis'},
				toolbox: {
					show : true,
					feature : {
						mark : {show: true},
						dataView : {show: true, readOnly: false},
						magicType: {show: true, type: ['line', 'bar', 'stack', 'tiled']},
						restore : {show: true},
						saveAsImage : {show: true}
					}
				},
				calculable : true,
				legend: {
					data:['\u652F\u4ED8\u91D1\u989D','\u5546\u6237\u5229\u6DA6','\u5E73\u53F0\u5229\u6DA6']
				},
				xAxis : [
					{
						type : 'category',
		        		boundaryGap: false,
						data : data.xAxisData
					}
				],
				yAxis : [
					{
						type : 'value',
						name : '\u91D1\u989D',
						axisLabel : {
							formatter: '{value} \u5143'
						}
					}
				],
				series : [
					{
						name:'\u652F\u4ED8\u91D1\u989D',
						type:'line',
			        	smooth:true,
			        	itemStyle: {normal: {areaStyle: {type: 'default'},label: {show: true}}},
						data:data.seriesDatas[0]
					},
					{
						name:'\u5546\u6237\u5229\u6DA6',
						type:'line',
			        	smooth:true,
			        	itemStyle: {normal: {areaStyle: {type: 'default'}}},
						data:data.seriesDatas[1]
					},
					{
						name:'\u5E73\u53F0\u5229\u6DA6',
						type:'line',
			        	smooth:true,
			        	itemStyle: {normal: {areaStyle: {type: 'default'},label: {show: true}}},
						data:data.seriesDatas[2]
					}
				]
			};
			myChart.setOption(option);
			window.addEventListener("resize", function() {
				myChart.resize();
			});
		}
	);
};

$.countDown = function (_this){
	var stamp = $.getCookie("SMS_TIMER");
	stamp = Number(stamp==""?0:stamp);
	var time = new Date().getTime();
	var second = Math.round((stamp - time)/1000);
	
	var timer = function (second, _this) {
		if (second == 0) {
			_this.off().sms(timer);
			_this.text("\u83B7\u53D6\u9A8C\u8BC1\u7801");
			_this.attr("disabled", false);
		}else {
			_this.attr("disabled", true);
			_this.text(second + "s\u540E\u91CD\u65B0\u83B7\u53D6");
			second --;
			setTimeout(function() {
				timer(second, _this);
			}, 1000);
		}
	};
	
	if (second <= 0) {
		_this.off().sms(timer);
		return;
	}

	timer(second, _this);
};

$.setCookie = function (cname, cvalue, exdays) {
	var d = new Date();
	d.setTime(d.getTime() + (exdays*24*60*60*1000));
	var expires = "expires="+d.toUTCString();
	document.cookie = cname + "=" + cvalue + "; " + expires;
};
$.getCookie = function (cname) {
	 var name = cname + "=";
	 var ca = document.cookie.split(';');
	 for(var i=0; i<ca.length; i++) {
	  var c = ca[i];
	  while (c.charAt(0)==' ') c = c.substring(1);
	  if (c.indexOf(name) != -1) 
		  return c.substring(name.length, c.length);
	 }
	 return "";
};