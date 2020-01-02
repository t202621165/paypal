var dataTable;
function PayPal(options){
	this.options = options;
	this._init();
}
PayPal.prototype._init = function (){
	if (this.options.rangePicker)
		$('.date-range-picker').daterangepicker();
	
	$('table th input:checkbox').on('click' , function(){
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox')
		.each(function(){
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});

	});
	
	$(".btn-query").on("click",function(){
		paypal.redraw();
	});
};
PayPal.prototype.getSettings = function (){
	var drawCallbacks = [];
	if (this.options.drawCallbacks) {
		drawCallbacks = this.options.drawCallbacks;
	}
	var rowCallbacks = [];
	if (this.options.rowCallbacks) {
		rowCallbacks = this.options.rowCallbacks;
	}
	var initCompletes = [];
	if (this.options.initCompletes) {
		initCompletes = this.options.initCompletes;
	}
	var url = this.options.ajax.url
	this.settings = {
			processing: true,
			autoWidth: false,
			searching: false,
			serverSide: true,
			ajax: this.options.ajax,
			columns: this.options.columns,
			order: [this.options.order],
			initComplete: function (settings){
				var jsonData = eval("("+settings.jqXHR.responseText+")");
				if (jsonData.recordsFiltered > 0){
					if (initCompletes.length > 0) {
						for (var i in initCompletes) {
							var render = initCompletes[i].render;
							if (typeof render == "function"){
								render(jsonData);
							}
						}
					}
				}
			},
			rowCallback: function(row, data, displayNum) {
				if (rowCallbacks.length > 0) {
					for (var i in rowCallbacks) {
						var render = rowCallbacks[i].render;
						if (typeof render == "function"){
							render(row, data);
						}
					}
				}
			},
			drawCallback : function (settings){
				$.page_init();
				$('table th input:checkbox').attr("checked", false);
				var jsonData = eval("("+settings.jqXHR.responseText+")");
				if (jsonData.recordsFiltered > 0){
					if (drawCallbacks.length > 0) {
						for (var i in drawCallbacks) {
							var render = drawCallbacks[i].render;
							if (typeof render == "function"){
								render(jsonData);
							}
						}
					}
					
				}
			}
		};
};


PayPal.prototype.paging = function (){
	this.options.ajax.data =  $("#searchForm").serializeData();
	this.getSettings();
	dataTable = $(this.options.element).DataTable(this.settings);
};

PayPal.prototype.dataTab = function (){
	this.paging();
	var settings = this.settings;
	$("#myTab li[data-tab-key]").on("click", function (){
		settings.ajax.data = $("#searchForm").serializeData();
		var _key = $(this).attr("data-tab-key");
		var _value = $(this).attr("data-tab-value");
		if (_key && _value){
			_value = _value == ""?null:_value;
			settings.ajax.data[_key] = _value;
		}else{
			settings.ajax.data.type = null;
			settings.ajax.data.state = null;
			settings.ajax.data.settlementType = null;
		}
		dataTable.destroy();
		dataTable =  $("#sample-table").DataTable(settings);
	});
};

PayPal.prototype.redraw = function (){
	this.settings.ajax.data = $("#searchForm").serializeData();
	if (this.settings.ajax.url == "funds/settlement") {
		if ($("#myTab").find(".active").attr("data-tab-value")){
			this.settings.ajax.data.state = 
				$("#myTab").find(".active").attr("data-tab-value");
		}
		
	}else if ($("#myTab").find(".first").length > 0){
		
		$("#myTab").find(".active").removeClass("active");
		$("#myTab").find(".first").addClass("active");
		
	}
	dataTable.destroy();
	dataTable =  $(this.options.element).DataTable(this.settings);
};