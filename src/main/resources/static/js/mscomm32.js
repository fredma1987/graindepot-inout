/**
 * Created by Administrator on 2019/2/12/012.
 */
var MSComm1;
String.prototype.Blength = function(){
    var arr = this.match(/[^\x00-\xff]/ig);
    return  arr == null ? this.length : this.length + arr.length;
};

function OperatePort() {
    MSComm1=document.getElementById("MSComm1");
    if($("#OperateButton").val()=="打开串口"){
        ConfigPort();
    }
    if(MSComm1.PortOpen==true)
    {
        try{
            MSComm1.PortOpen=false;
            document.getElementById("OperateButton").value="打开串口";
        }catch(ex)
        {
            alert(ex.message);
        }
    }else{
        try{
            MSComm1.PortOpen=true;
            document.getElementById("OperateButton").value="关闭串口";
        }catch(ex)
        {
            alert(ex.message);
        }
    }
}

function ConfigPort()
{
    if(MSComm1.PortOpen==false||MSComm1.PortOpen==undefined)
    {
        try{
            //默认串口2
            MSComm1.CommPort="1";
            //默认波特率
            var BaudRate="9600";
            //默认校验位
            var CheckBit="N";
            //默认数据位
            var DataBits="8";
            //默认停止位
            var StopBits="1";
            MSComm1.Settings=BaudRate+ ","+CheckBit+","+DataBits+","+StopBits;
            MSComm1.OutBufferCount =0;           //清空发送缓冲区
            MSComm1.InBufferCount = 0;           //滑空接收缓冲区
        }catch(ex){
            alert(ex.message);
        }
    }
    else{
        alert("请先关闭串口后再设置！");
    }
}

function Send()
{
    //alert(document.getElementById("txtSend").value);
    var orgstr=document.getElementById("txtSend").value;
    var newstr="";
    var hexflag=document.getElementById("isSendHex").checked;
    if(hexflag&&orgstr!="")
    {
        if(orgstr.substr(0,2)=="0x"||orgstr.substr(0,2)=="0X") orgstr=orgstr.substring(2,orgstr.length);
        if(orgstr.length%2!=0) orgstr="0"+orgstr;
        // TODO  str2hex判断16进制的方法
        alert(str2hex(orgstr));
        if((newstr=str2hex(orgstr))=="") {alert("错误的16进制数");return false;}
    }
    try{
        MSComm1.Output=hexflag?newstr:orgstr;
        //MSComm1.Output = "AA 00 03 88 18 0A 99 BB";
    }catch(ex)
    {alert(ex.message);}
}

function Receive()
{
    var hex=str2hex(MSComm1.Input);
    if(hex.indexOf("%02")>-1){
        var data=hex.split("%");
        var qty="";
        if(data.length==9){
          for(var i=3;i<data.length;i++){
              qty+=String.fromCharCode(parseInt(data[i],16));
          }
          if(hex.indexOf("%2B")>-1){
            document.getElementById("weight").value = parseInt(qty);
          }else{
            qty="-"+qty;
            document.getElementById("weight").value = parseInt(qty);
          }
        }
        
    }
}

function ClearReceived()
{
    document.getElementById("weight").innerText="";
}
// 转化成16进制
function str2hex(s) {
    var a,b,d;
    var hexStr = '';
    for (var i=0; i < s.length; i++) {
        d = s.charCodeAt(i);
        a = d % 16;
        b = (d - a)/16;
        hexStr += '%' + "0123456789ABCDEF".charAt(b) + "0123456789ABCDEF".charAt(a);
    }
    return hexStr;
}
function charCode(v){
    return String.fromCharCode(v);
}
function checkInstallOCx() {
    try{
        var   obj = new ActiveXObject("MSCOMMLib.MSComm.1");
        alert("已经注册");
    }
    catch(e)
    {
        alert("没有注册");
    }
}

