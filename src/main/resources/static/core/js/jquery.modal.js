jQuery.fn.ModalBox = function(options){
	this.title = options.title;
	this.forms = options.forms;
	this._flash = options._flash;
	this.url = options.url;
	this.hideData = options.hideData;
	this._init();
};
jQuery.fn.extend({
	_init: function (){
		$("#modal-table .modal-title").text(this.title);
		$(this).on("click.ModalBox",$.proxy(this.content, this));
		
	},
	doc: function (title){
		this._group_header = '<div class="form-group">' + 
						'<label for="" class="col-sm-1 control-label"></label>' + 
						'<div class="col-sm-10">' + 
							'<div class="input-group">' + 
								'<span class="input-group-addon">'+ title +'</span>' + 
								'<span class="block input-icon input-icon-right">';
		this._group_foot = '<i class="fa fa-remove red i-prt"></i></span></div></div></div>';
	},
	finish: function (doc){
			var _html = this._group_header + doc + this._group_foot;
		$("#modal-form").append(_html);
	},
	content: function (){
		if (this.forms){
			$("#modal-form").empty();
			$("#modal-save").off().on("click.ModalBox",$.proxy(this.validate, this));
			if (this.hideData) {
				for (var index in this.hideData) {
					var _hide = this.hideData[index];
					$("#modal-form").append("<input type='hidden' name='"+_hide.name+"' value='"+_hide.value+"'>");
				}
			}
			for (var index in this.forms) {
				var _form = this.forms[index];
				this.doc( _form.title);
				var _doc = "";
				var readonly = _form.readonly?"readonly":"";
				if (_form.doc == "input"){
					_doc = "<input class='form-control radius-right' type='"+_form.docType+"' "+readonly+" name='"+_form.name+"' " +
							"value='"+(_form.value?_form.value:'')+"' "+(_form.nullable?"nullable='true'":"")+" placeholder='\u8BF7\u8F93\u5165"+_form.title+(_form.nullable?"(\u53EF\u7A7A)":"")+"'>";
				}
				if (_form.doc == "dbinput"){
					_doc = '<span class="none">\u6700\u5C0F\u4ED8\u6B3E\u91D1\u989D</span><input name="'+_form.names[0]+'" type="'+_form.docType+'" value="'+(_form.values?_form.values[0]:'')+'" class="form-control search-query radius-none" placeholder="0">'+
							'<i class="fa fa-remove red i-prt"></i></span>'+
							'<span class="input-group-addon" style="background: none;border: none;padding: 5px;">-</span><span class="none">\u6700\u5927\u4ED8\u6B3E\u91D1\u989D</span>'+
							'<span class="block input-icon input-icon-right">'+
							'<input name="'+_form.names[1]+'" type="'+_form.docType+'" value="'+(_form.values?_form.values[1]:'')+'" class="form-control search-query radius-right" placeholder="999...">';
				}
				if (_form.doc == "textarea"){
					_doc = '<textarea class="form-control radius-right" '+(_form.nullable?"nullable='true'":"")+' name="'+_form.name+'" rows="3" placeholder="\u8BF7\u8F93\u5165'+_form.title+(_form.nullable?"(\u53EF\u7A7A)":"")+'">'+(_form.value?_form.value:'')+'</textarea>';
				}
				if (_form.doc == "select"){
					_doc = "<select class='form-control radius-right' name='"+_form.name+"'>";
					if (_form.callback && typeof _form.callback == "function"){
						_doc += _form.callback(_form.data);
					}
					_doc += "</select>";
				}
				this.finish(_doc);
			}
		}
	},
	validate: function (){
		var arr = $("#modal-form .form-control");
		for (var i=0 ; i < arr.length; i++) {
			var ele = arr[i];
			var value = ele.value;
			var nullable = ele.getAttribute("nullable");
			if (value == "" && !nullable){
				$(ele).focus().next("i.i-prt").css("display","block");
				var title = $(ele).parent().prev().text();
				layer.msg("\u201C"+ title+ "\u201D\u4E0D\u80FD\u4E3A\u7A7A\uFF01",{
					skin: "layui-layer-error",time: 1500,offset: "50px"
				});
				return;
			}else{
				$(ele).next("i.i-prt").css("display","none");
			}
		}
		if (this.url != undefined && this.url != "")
			this.submit();
	},
	submit: function (){
		$("#modal-save").attr("disabled",true);
		var isFlash = this._flash;
		$.ajax({
			type: "POST",
			url: this.url,
			data: $("#modal-form").serialize(),
			success: function (data){
				$("#modal-save").attr("disabled",false);
				if (data.state){
					$("#modal-table").modal("hide");
					layer.msg(data.msg,{
						icon: 1,time: 1500
					});
					if ($("#sample-table").length > 0) {
						$("#sample-table").dataTable().fnDraw(false);
					}
				}else{
					layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> "+data.msg,{
						skin: "layui-layer-error",time: 1500
					});
				}
				if(isFlash)
					window.location.reload();
			},
			error: function (){
				$("#modal-table").modal("hide");
				$("#modal-save").attr("disabled",false);
				layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> \u7CFB\u7EDF\u5F02\u5E38\uFF01",{
					skin: "layui-layer-exception",time: 1500,offset: "50px"
				});
			}
		});
	},
	sms: function (timer){
		$(this).on("click", function (){
			var _this = $(this);
			$.postAjax({
				success:true,
				url: "sms",
				data: {is_effective: $("#is_effective").is(":checked")},
				callback: function (data){
					if (data.msg == "IS_EFFECTIVE") {
						layer.msg("\u9A8C\u8BC1\u7801\u6682\u672A\u5931\u6548\uFF0C\u82E5\u91CD\u65B0\u83B7\u53D6\u8BF7\u5148\u5173\u95ED\u2018\u65F6\u6548\u9A8C\u8BC1\u2019\uFF01");
					}else {
						layer.msg(data.msg,{offset: "150px"});
						var d = new Date();
						var stamp = d.getTime() + 60 * 1000;
						$.setCookie("SMS_TIMER", stamp, 1/(24*60));
						timer(60, _this);
					}
					return true;
				}
			});
		});
	}
});