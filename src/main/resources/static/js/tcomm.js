/**
 * Created by Administrator on 2019/2/12/012.
 */
//document.write(navigator.userAgent);
var tcom=null;
$(function(){
    comlist();
});
function closeCom(){
    tcom.Close(function(){
        //alert("已关闭")
    });
}
function send(){
    if(CUR_SJT!="HEX")
        tcom.Send($("#t_sdata").val()+"\r",function(dat){if(dat.STAT==1){
            //alert("发送成功！")
        }else{alert("发送失败！")}});//往端口发送数据
    else
        tcom.Send($("#t_sdata").val(),function(dat){if(dat.STAT==1){
            //alert("发送成功！")
        }else{alert(dat.STAT+"发送失败！")}});//往端口发送数据
}
function comlist() {
    var objTComm = new TComm("COM1", "9600,N,8,1");
    objTComm.Register("9060a7236f9a5ed7c764983a826076ebf6dfa1f115611d3934c58749de06036233bd174ef8756ec99f8b526d113733c2839427307b72ad00c6e9c18b0fa62918439522399f65257d", function (dat) {
        if (dat.STAT == 11) {
            objTComm.getComList(function (dat) {
                if(!dat.COMS){comlist();return;}
                if(dat.COMS.length>0){
                    $(dat.COMS).each(function(i){
                        $("<option value='"+dat.COMS[i].PName.substr(3)+"'>"+dat.COMS[i].PName+"("+dat.COMS[i].FName+")"+"</option>").insertBefore($("#COMLI option:first"))
                    });
                    document.getElementById("COMLI").selectedIndex=0;
                }
            });}
    });
}
//选择并打开端口
var CUR_SJT = "";
function selcomport() {//tcom.SelectComm()
    CUR_SJT = $("#ssjt").val();
    //默认串口2
    var comNo = 2;
    /*if ($("#COMLI").val() != "-1") {
        comNo = $("#COMLI").val();
    } else {
        alert("请选择串口！"); return;
    }*/
    //波特率
    var sbtl="115200";
    //校验位
    var sjyw="N";
    //数据位
    var ssjw="8";
    //停止位
    var stzw="1";
    var comSet = sbtl + "," + sjyw + "," + ssjw + "," +stzw;
    if ($("#btnOpen").val() == "关闭串口") {
        closeCom();
        $("#btnOpen").val("打开串口");

    } else {
        tcom = new TComm(comNo, comSet, $("#ssjt").val(),10);
        tcom.OnDataIn = function (dat) { //接收串口返回数据
            if ($("#weight").val().length > 10000) $("#weight").val("");
            if (CUR_SJT == "hex") {
                $("#weight").val($("#weight").val() +"\n" + dat.data);
            } else {
                $("#weight").val($("#weight").val() +"\n" + dat.data);
            }
            if($("#weight").val().substr($("#weight").val().length-9)=="0000027A9")$("#weight").val($("#weight").val()+"\n");
        };
        tcom.Register("9060a7236f9a5ed7c764983a826076ebf6dfa1f115611d3934c58749de06036233bd174ef8756ec99f8b526d113733c2839427307b72ad00c6e9c18b0fa62918439522399f65257d", function (dat) {
            if (dat==-99||dat.STAT == -99) {
                if (confirm("您还没有安装串口插件\n\n现在下载安装吗？")) {
                    location = "/file/TComm2.exe";
                }
            } else if (dat.STAT == 11) {
                tcom.init(function (ret) {
                    if (ret.STAT == 1) {
                        $("#btnSend").attr("disabled", false); $("#btnOpen").val("关闭串口");
                    } else {
                        alert("打开串口失败!");
                    }
                });
            } else {
                alert("注册失败,请与您的服务商联系!");
            }
        });
    }
}
