function mapping(){
	return new Mapping();
}
function Mapping(){}
Mapping.prototype.merchantState = function (){
	$(".merchant-state").on("click", function (){
		var data = eval("("+this.getAttribute("data-ajax-data")+")");
		$.postAjax({
			error: true,
			success: true,
			data: data,
			url: this.getAttribute("data-ajax-url"),
			before: function (){
				$(".merchant-state").attr("disabled", true);
			},
			callback: function (){
				$(".merchant-state").attr("disabled", false);
				$("#sample-table").dataTable().fnDraw(false);
			}
		});
	});
};
Mapping.prototype.angle = function (){
	$(".angle-left").on("click", function (){
		if ($(".show-5").is(":visible")&&document.body.clientWidth > 540){
			$(this).addClass("none").removeClass("summary");
			$(".alert .col-xs-2").animate({},function(){
				var isHidden = $(this).is(":hidden");
				$(this).addClass(isHidden?"summary":"none").removeClass(isHidden?"none":"summary")
			});
			$(".angle-right").addClass("summary").removeClass("none");
		}else if ($(".show-5").is(":visible")){
			$(".show-3,.show-4").removeClass("none").addClass("summary");
			$(".show-5").removeClass("summary").addClass("none");
			$(".angle-right").addClass("summary").removeClass("none");
		}else{
			$(this).addClass("none").removeClass("summary");
			$(".show-2").removeClass("none").addClass("summary");
			$(".show-3,.show-4").removeClass("summary").addClass("none");
		}
	});
	$(".angle-right").on("click", function(){
		var hidden = $(".alert .col-xs-2:hidden").length;
		$(".angle-left").addClass("summary").removeClass("none");
		if (hidden < 3){
			$(this).addClass("none").removeClass("summary");
			$(".alert .col-xs-2").animate({},function(){
				var isHidden = $(this).is(":hidden");
				$(this).addClass(isHidden?"summary":"none").removeClass(isHidden?"none":"summary");
			});
		}else{
			if ($(".show-2").is(":hidden")){
				$(this).addClass("none").removeClass("summary");
				$(".show-3,.show-4").removeClass("summary").addClass("none");
				$(".show-5").removeClass("none").addClass("summary");
			}else{
				$(".show-2").removeClass("summary").addClass("none");
				$(".show-3,.show-4").removeClass("none").addClass("summary");
			}
		}
	});
	window.addEventListener("resize", function() {
		$(".alert .col-xs-2,a").removeClass("summary").removeClass("none");
	});
};

Mapping.prototype.settlements = function (){
	$(".btn-settlement").on("click",function(){
		var element = $(this).parent().parent().find(".serialize");
		var data = {};
		var serializeData = element.serializeArray();
		for (var index in serializeData) {
			var _name = serializeData[index].name;
			var _value = serializeData[index].value;
			data[_name] = _value;
		}
		var _this = $(this)
		var list = [data];
		layer.confirm('\u662F\u5426\u786E\u5B9A\u7ED3\u7B97\uFF1F', {
			title: "<li class='fa fa-question-circle-o bigger-120 red'></li> \u63D0\u793A",
			btn: ['\u786E\u5B9A', '\u53D6\u6D88'] //\u6309\u94AE
		}, function(){
			$.pay_post(_this, list)
		});
	});
};
Mapping.prototype.batch = function (){
	$(".btn-batch").off("click").on("click",function(){
		var array = $("tbody input:checkbox:checked");
		if (array.length > 0){
			var list = new Array();
			for (var i = 0; i < array.length; i++) {
				var element = $(array[i]).parent().parent().parent().find(".serialize");
				var data = {};
				var serializeData = element.serializeArray();
				for (var index in serializeData) {
					var _name = serializeData[index].name;
					var _value = serializeData[index].value;
					data[_name] = _value;
				}
				list.push(data);
			}
			var _this = $(this)
			layer.confirm('\u662F\u5426\u786E\u5B9A\uFF1F', {
				title: "<li class='fa fa-question-circle-o bigger-120 red'></li> \u63D0\u793A",
				btn: ['\u786E\u5B9A', '\u53D6\u6D88'] //\u6309\u94AE
			}, function(){
				$.pay_post(_this, list)
			});
		}else{
			layer.msg("\u8BF7\u9009\u62E9\u6279\u91CF\u64CD\u4F5C\u8BB0\u5F55\uFF01");
		}
	});
};

Mapping.prototype.batchAlipay = function (){
	$(".btn-alipay").off("click").on("click",function(){
		var array = $("tbody input:checkbox:checked");
		if (array.length > 0){
			var list = new Array();
			for (var i = 0; i < array.length; i++) {
				var element = $(array[i]).parent().parent().parent().find(".serialize");
				var data = {};
				var serializeData = element.serializeArray();
				for (var index in serializeData) {
					var _name = serializeData[index].name;
					var _value = serializeData[index].value;
					data[_name] = _value;
				}
				list.push(data);
			}
			var _this = $(this)
			layer.confirm('\u662F\u5426\u786E\u5B9A\uFF1F', {
				title: "<li class='fa fa-question-circle-o bigger-120 red'></li> \u63D0\u793A",
				content : "<input type = 'text' id = 'code'/> <Button id = 'sms'>获取验证码</Button>",
				btn: ['\u786E\u5B9A', '\u53D6\u6D88'] //\u6309\u94AE
			}, function(){
				$.ajax({
					url:"payee/valid/code",
					type:"post",
					data:{"code":$("#code").val()},
					success:function(data){
						if (data.state){
							$.pay_post(_this, list);
						}else{
							alert(data.msg);
						}
					}
				});
				
			});
		}else{
			layer.msg("\u8BF7\u9009\u62E9\u6279\u91CF\u64CD\u4F5C\u8BB0\u5F55\uFF01");
		}
		
		$("#sms").off("click").on("click",function(){
			$.ajax({
				url:"sms",
				type:"post",
				success:function(data){
					if (data) {
						$.countDown($("#sms"));
						alert(data.msg);
						//layer.msg(data.msg,{icon: 1});
					}
				}
			});
		});
	});
};


Mapping.prototype.batchAudit = function (){
	$(".btn-audit").off("click").on("click",function(){
		var array = $("tbody input:checkbox:checked");
		if (array.length > 0){
			var list = new Array();
			for (var i = 0; i < array.length; i++) {
				var element = $(array[i]).parent().parent().parent().find(".serialize");
				var data = {};
				var serializeData = element.serializeArray();
				for (var index in serializeData) {
					var _name = serializeData[index].name;
					var _value = serializeData[index].value;
					data[_name] = _value;
				}
				list.push(data);
			}
			var _this = $(this)
			layer.confirm('\u662F\u5426\u786E\u5B9A\uFF1F', {
				title: "<li class='fa fa-question-circle-o bigger-120 red'></li> \u63D0\u793A",
				content : "<input type = 'text' id = 'code'/> <Button id = 'sms'>获取验证码</Button>",
				btn: ['\u786E\u5B9A', '\u53D6\u6D88'] //\u6309\u94AE
			}, function(){
				$.ajax({
					url:"payee/valid/code",
					type:"post",
					data:{"code":$("#code").val()},
					success:function(data){
						if (data.state){
							$.pay_post(_this, list);
						}else{
							alert(data.msg);
						}
					}
				});
				
			});
		}else{
			layer.msg("\u8BF7\u9009\u62E9\u6279\u91CF\u64CD\u4F5C\u8BB0\u5F55\uFF01");
		}
		
		$("#sms").off("click").on("click",function(){
			$.ajax({
				url:"sms",
				type:"post",
				success:function(data){
					if (data) {
						$.countDown($("#sms"));
						alert(data.msg);
						//layer.msg(data.msg,{icon: 1});
					}
				}
			});
		});
	});
};


Mapping.prototype.amountCheck = function (row, amount, rowNo, inputName){
	$('td:eq('+ rowNo +')', row)
		.find("input[name="+inputName+"]")
			.on("keyup blur",function(){
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
		if (amount){
			if (this.value == ""){
				this.value = 0.00;
			}
			if (this.value > amount){
				this.value = amount;
			}
		}
	});
};
Mapping.prototype.editPayee = function (row, data){
	$('td:eq(6)', row).find(".btn-edit").ModalBox({
		title: "\u7F16\u8F91\u4EE3\u4ED8\u901A\u9053",
		url: "payee/edit",
		hideData: [{name: "id",value: data.id}],
		forms: [
			{title: "\u4EE3\u4ED8\u540D\u79F0",name: "name",doc: "input",docType: "text",value: data.name},
			{title: "\u4EE3\u4ED8\u7C7B\u578B",name: "type",doc: "select",callback: function (){
				return "<option value=''>--\u8BF7\u9009\u62E9--</option>" + 
				"<option value='true' "+(data.type?"selected":"")+">\u652F\u4ED8\u5B9D</option>" + 
				"<option value='false' "+(data.type?"":"selected")+">\u94F6\u8054</option>";
			}},
			{title: "\u552F\u4E00\u6807\u8BC6",name: "mark",doc: "input",docType: "text",value: data.mark},
			{title: "\u4ED8\u6B3E\u91D1\u989D",names: ["minAmount","maxAmount"],doc: "dbinput",docType: "text",
					values: [data.minAmount,data.maxAmount]},
			{title: "\u4EE3\u4ED8\u8D26\u53F7",name: "account",doc: "input",docType: "text",value: data.account},
			{title: "\u4ED8\u6B3E\u5907\u6CE8",name: "remark",doc: "input",docType: "text",value: data.remark},
			{title: "\u5BC6\u94A5",name: "signKey",doc: "input",docType: "text",nullable: true,value: data.signKey},
			{title: "\u516C\u94A5",name: "publicKey",doc: "textarea",nullable: true,value: data.publicKey},
			{title: "\u79C1\u94A5",name: "privateKey",doc: "textarea",nullable: true,value: data.privateKey}
		]
	});
};
Mapping.prototype.editMerBank = function (row, data){
	$('td:eq(7)', row).find(".btn-edit").ModalBox({
		title: "编辑账户",
		url: "bank/edit",
		hideData: [{name: "id", value: data.id}],
		forms: [
			{title: "开户银行",name: "bankMark",doc: "select",callback: function (){
				var result = "<option value=''>--请选择--</option>";
				var bankMark = data.bankMark;
				for (var i = 0; i < banks.length; i++) {
					var bank = banks[i];
					var selected = bankMark==bank.mark?"selected":"";
					result += "<option value='"+bank.mark+"' "+selected+">"+bank.name+"</option>"
				}
				return result;
			}},
			{title: "开户地区",name: "bankBranch",doc: "input",docType: "text",value: data.bankBranch},
			{title: "银行账号",name: "bankNumber",doc: "input",docType: "text",value: data.bankNumber},
			{title: "账户名　",name: "realName",doc: "input",docType: "text",value: data.realName},
			{title: "身份证号",name: "idNumber",doc: "input",docType: "text",value: data.idNumber}
		]
	});
};
Mapping.prototype.orderDetails = function (row, data){
	var stateData = {
		"state0": [
			{title: "\u7B49\u5F85\u4ED8\u6B3E",step: "<i class='fa fa-times' style='color: #949ea7;'></i>"},
			{title: "\u8BA2\u5355\u5B8C\u6210",step: "<i class='fa fa-times' style='color: #949ea7;'></i></i>"}
		],
		"state1": [
			{title: "\u4ED8\u6B3E\u6210\u529F",step: "<i class='fa fa-check'></i>"},
			{title: "\u8BA2\u5355\u5B8C\u6210",step: "<i class='fa fa-check'></i>"}
		],
		"state2": [
			{title: "\u4ED8\u6B3E\u6210\u529F",step: "<i class='fa fa-check'></i>"},
			{title: "\u7B49\u5F85\u53D1\u9001",step: "<i class='fa fa-times' style='color: #949ea7;'></i></i>"}
		]
	}
	$('td:eq(2)', row).find("a").off().on("click", function (){
		var state_li = $(".wizard-steps").find("li");
		state_li.removeClass("active");
		var _details =$("#orderDetails");
		$("input[name=id]").val(data.id);
		$.orderSend(data.id);
		$.orderRefund(data.sysOrderNumber);
		for (var key in data) {
			var value = data[key];
			
			if (key == "state") {
				if (value == 1){
					$(".wizard-steps").find("li").addClass("active");
				}else if (value == 2){
					$(".state1,.state2").addClass("active");
				}else{
					$(".state1").addClass("active");
				}
				var titles = _details.find(".state-title");
				var setps = _details.find(".state-step");
				$(titles[0]).empty().append(stateData["state"+value][0].title);
				$(titles[1]).empty().append(stateData["state"+value][1].title);
				$(setps[0]).empty().append(stateData["state"+value][0].step);
				$(setps[1]).empty().append(stateData["state"+value][1].step);
			}
			
			if (key == "merchantId")
				$(".merchant").attr("href","profile_"+value+".html");
			
			var _element = _details.find("#"+key);
			if (_element.length > 0) {
				value==null?"&nbsp;":value;
				if (key == "amount")
					value = $.toThousands(value);
				_element.empty().append(value == null?"--":value);
			}
		}
	});
};
Mapping.prototype.orderSend = function (row ,data){
	$('td:eq(6)', row).find("a").on("click", function (){
		var _this = $(this);
		$.send(_this, {id: data.id});
	});
};

$.orderRefund = function(sysNumber){
	$(".order-refund").off().on("click",function(){
		layer.open({
			id:1,
				offset: 'rb',
		        title:'\u652F\u4ED8\u5B9D\u9000\u6B3E',
		        skin:'layui-layer-rim',     
		        content: '<input id="party" type = "text" style = "width:100%;height:25px;" placeholder="\u652F\u4ED8\u51ED\u8BC1 \u4F8B\u5982:2018063015570123456012">'+
		        	'<textarea id="discription" rows="3" cols="45"  placeholder="\u9000\u6B3E\u7406\u7531 \u4F8B\u5982:\u534F\u5546\u9000\u6B3E" style="width:100%;margin-top:10px;"></textarea>',
		        btn:['\u9000\u6B3E','\u53D6\u6D88'],
		        btn1: function (index,layero) {
		        	$.ajax({
						  type:"POST",
						  url:"/order/refund",
					  	  data:{
					  		  "partyOrderNumber":$("#party").val(),
					  		  "sysOrderNumber":sysNumber,
					  		  "orderDiscription":$("#discription").val()
					  	  },
					  	  success:function(data){
					  		  if(data.code == '10041'){
					  			var top = (window.screen.height-30-600)/2; //获得窗口的垂直位置;  
					  			var left = (window.screen.width-10-1200)/2; //获得窗口的水平位置;  
					  			window.open(data.url,'newwindow','height=600,width=1200,top='+top+',left='+left+',toolbar=no,menubar=no,scrollbars=no,resizable=no,location=no, status=no');
					  			layer.close(index);
					  		  }else{
					  			layer.msg(data.msg,{offset: "120px",time: 2000,skin: "layui-layer-error"});
					  			layer.close(index);
					  		  }
					  	  }
					  });
		    	},
		        btn2:function (index,layero) {
		        	 layer.close(index);
		        }
		    });
	});
}

$.orderSend = function (id){
	$(".order-send").off().on("click", function (){
		var _this = $(this);
		$.send(_this, {id: id});
	});
};
$.send = function (_this,data){
	layer.confirm('\u786E\u5B9A\u5BF9\u8BE5\u8BA2\u5355\u8865\u53D1\uFF1F', {
		title: "<li class='fa fa-question-circle-o bigger-120 red'></li> \u63D0\u793A",
		btn: ['\u786E\u5B9A', '\u53D6\u6D88'] //\u6309\u94AE
	}, function(){
		$.postAjax({
			error: true,
			success: true,
			data: data,
			url: "order/send",
			before: function (){
				_this.attr("disabled", true);
			},
			callback: function (){
				_this.attr("disabled", false);
			}
		});
	});
};

Mapping.prototype.merchantGallery = function (data){
	$.postAjax({
		error: true,
		success: true,
		url: "merchant/gallery",
		data: data,
		before: function (){
			$(".widget-box-overlay").removeClass("none");
		},
		callback: function (){
			$(".widget-box-overlay").addClass("none");
		}
	});
};
Mapping.prototype.merchantGallerys = function (data){
	$.postAjax({
		error: false,
		success: false,
		url: "merchant/gallerys",
		data: data
	});
};
Mapping.prototype.fundsList = function (mark,data){
	if (data == "")
		return "0.00";
	for (var index in data) {
		var obj = data[index];
		if (obj.productMark == mark)
			return $.toThousands(obj.merchantProfits)+' <i class="fa fa-caret-down" '+
			'data-rel="popover" data-trigger="hover" data-placement="bottom" ' + 
			'data-title="\u652F\u4ED8\u91D1\u989D\uFF1A'+$.toThousands(obj.amount)+'" data-content="\u5E73\u53F0\u5229\u6DA6\uFF1A'+
			$.toThousands(obj.platformProfits)+'"></i>';
	}
	return "0.00";
};
$.fn.fundsPayee = function (){
	$(this).on("click", function (){
		var _this = $(this);
		var arr = $(".required");
		for (var i = 0; i < arr.length; i++) {
			var _arr = arr[i];
			var value = _arr.value;
			if (value == "") {
				var title = _arr.getAttribute("data-title");
				$(_arr).focus();
				$(_arr).next("i.i-prt").css("display", "block");
				layer.msg("<li class='fa fa-exclamation-triangle bigger-130'></li> \u201C"+title+"\u201D\u4E0D\u80FD\u4E3A\u7A7A\uFF01",
						{time: 1500,skin: "layui-layer-error",offset: "100px"});
				return;
			}else{
				$(_arr).next("i.i-prt").css("display","none");
			}
		}
		$.postAjax({
			success:true,
			error:true,
			url: "payee/pay",
			data: $("#modal-form").serialize(),
			before: function (){
				_this.attr("disabled", true);
			},
			callback: function (data){
				_this.attr("disabled", false);
				if (data) {
					paypal.redraw();
					layer.msg(data.msg,{icon: 1});
					return true;
				}
			}
		});
	});
}
Mapping.prototype.fundsListBank = function (data){
	if (data == "")
		return "0.00";
	var amount = 0.00;
	var merchantProfits = 0.00;
	var platformProfits = 0.00;
	for (var index in data) {
		var obj = data[index];
		if (obj.type == 1) {
			amount += obj.amount;
			merchantProfits += obj.merchantProfits;
			platformProfits += obj.platformProfits;
		}
	}
	return $.toThousands(merchantProfits)+' <i class="fa fa-caret-down" '+
	'data-rel="popover" data-trigger="hover" data-placement="bottom" ' + 
	'data-title="\u652F\u4ED8\u91D1\u989D\uFF1A'+$.toThousands(amount)+'" data-content="\u5E73\u53F0\u5229\u6DA6\uFF1A'+
	$.toThousands(platformProfits)+'"></i>';
};
$.pay_post = function (_this,list){
	$.postAjaxByJson({
		error: true,
		success: true,
		data: JSON.stringify(list),
		url: _this.attr("data-ajax-url"),
		before: function (){
			_this.attr("disabled", true);
			$(".widget-box-overlay").removeClass("none");
		},
		callback: function (){
			_this.attr("disabled", false);
			$(".widget-box-overlay").addClass("none");
			try {
				if (paypal) {
					paypal.redraw();
				}
			} catch (e) {
				console.log(e.message);
			}
		}
	});
};