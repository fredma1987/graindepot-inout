//根据inout生成tooltip框
function getInoutTooltipHtml(inout) {
    var billdatestr = inout.billdatestr;
    var items = [];
    //登记
    items.unshift({time: inout.regtimestr.split(" ")[1], text: "登记"});
    //扦样
    if (inout.samplingstate == 1) {
        items.unshift({time: inout.samplingtimestr.split(" ")[1], text: "扦样"});
    }
    //检验
    if (inout.inspectstate == 1) {
        items.unshift({time: inout.inspecttimestr.split(" ")[1], text: "检验"});
    }
    //称毛重
    if (inout.gwstate == 1) {
        items.unshift({time: inout.gwtimestr.split(" ")[1], text: "称毛重"});
    }
    //值仓中
    if (inout.valuebinstate == 2) {
        items.unshift({time: inout.valuebinstarttimestr.split(" ")[1], text: "值仓中"});
    }
    //已值仓
    if (inout.valuebinstate == 1) {
        items.unshift({time: inout.valuebinendtimestr.split(" ")[1], text: "值仓"});
    }
    //称皮重
    if (inout.tarestate == 1) {
        items.unshift({time: inout.taretimestr.split(" ")[1], text: "称皮重"});
    }
    //结算
    if (inout.paidstate == 1) {
        items.unshift({time: inout.paidtimestr.split(" ")[1], text: "结算"});
    }
    //退卡
    if (inout.backicflag == 1) {
        items.unshift({time: inout.backictimestr.split(" ")[1], text: "退卡"});
    }
    //终止
    if (inout.stopflag == 1) {
        items.unshift({time: "", text: "已终止"});
    }
    var timeLineHtml = "";
    timeLineHtml += '<div class="main-container" id="main-container">' +
        '<div class="timeline-container timeline-style2">' +
        '<span class="timeline-label">' +
        '<b id="billdate">' + billdatestr + '</b></span>';
    items.forEach(function (value, index) {
        timeLineHtml+='<div class="timeline-item clearfix">' +
            '<div class="timeline-info">' +
            '<span class="timeline-date">'+value.time+
            '</span>' +
            '<i class="timeline-indicator btn btn-info"></i>' +
            '</div>' +
            '<div class="widget-box transparent">' +
            '<div class="widget-body">' +
            '<div class="widget-main no-padding">' +
            '完成' +
            '<span class="label label-sm label-success">'+
            value.text+'</span>' +
            '</div></div></div></div>'
    });
    timeLineHtml+='</div>';
    timeLineHtml+='</div>';
    return timeLineHtml;

}