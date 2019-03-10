/**
 * Created by CC on 2017/9/13.
 */
var PortOpened = 0;
var CpuCardFound = 0;

function init() {
    
}


function idCard() {
    var result;
    var obj = {};
    //注意：第一个参数为对应的设备端口，USB型为1001，串口型为1至16
    //result=IdrControl1.ReadCard("1001","c:\\test.jpg");
    result = IdrControl1.ReadCard("1001", "");
    if (result == 1) {
        obj.name = IdrControl1.GetName();
        obj.idnumber = IdrControl1.GetCode();

        /*document.all.oTable.rows(1).cells(1).innerText=IdrControl1.GetName();
        document.all.oTable.rows(2).cells(1).innerText=IdrControl1.GetFolk();
        document.all.oTable.rows(3).cells(1).innerText=IdrControl1.GetSex();
        document.all.oTable.rows(4).cells(1).innerText=IdrControl1.GetBirthYear() + "年" + IdrControl1.GetBirthMonth() + "月" + IdrControl1.GetBirthDay() +  "日";
        document.all.oTable.rows(5).cells(1).innerText=IdrControl1.GetCode();
        document.all.oTable.rows(6).cells(1).innerText=IdrControl1.GetAddress();
        document.all.oTable.rows(7).cells(1).innerText=IdrControl1.GetAgency();
        document.all.oTable.rows(8).cells(1).innerText=IdrControl1.GetValid();
        document.all.oTable.rows(9).cells(1).innerText=IdrControl1.GetJPGPhotobuf();
        document.all.oTable.rows(10).cells(1).innerText=IdrControl1.GetSAMID();
        document.all.oTable.rows(11).cells(1).innerText=IdrControl1.GetIDCardSN();

        document.all.notbook.src="pic.htm";*/

        //alert(IdrControl1.GetSexN());
        //alert(IdrControl1.GetFolkN());

    } else {
        if (result == -1)
            alert("端口初始化失败！");
        if (result == -2)
            alert("请重新将卡片放到读卡器上！");
        if (result == -3)
            alert("读取数据失败！");
        if (result == -4)
            alert("生成照片文件失败，请检查设定路径和磁盘空间！");

    }
    return obj;
}

function jlReadIcCard(position) {
    var result;
    //注意：参数为对应的设备端口，iDR210为8159，iDR200 USB型为1001，iDR200串口型为1至16
    result = IdrControl1.ReadICCard("8159");
    if (result == 1) {
        var o=IdrControl1.ReadTypeABlock(position/4,position%4,1,"FFFFFFFFFFFF");
        var real=o.replaceAll("00","");
        return utf8HexToStr(real);
    } else {
        if (result == -1){
            $.bootstrapBox.alert.init({message:"端口初始化失败！"});
        }
        if (result == -2){
            $.bootstrapBox.alert.init({message:"读IC卡失败"});
        }
        return "ERROR";
    }
}

function jlReadIcCardAll() {
    var results = [];
    for (var i = 0; i <= 15; i++) {
        for (var j = 0; j <= 2; j++) {
            if ((i + 1) % 4 != 0) {
                var position = i * 4 + j;
                var data=IdrControl1.ReadTypeABlock(position/4,position%4,1,"FFFFFFFFFFFF")
                results.push({
                    index: i + "_" + j,
                    data: utf8HexToStr(data.replaceAll("00",""))
                });
            }
        }
    }
    return results;
}

function jlReadIcCardNum() {
    var result;
    //注意：参数为对应的设备端口，iDR210为8159，iDR200 USB型为1001，iDR200串口型为1至16
    result = IdrControl1.ReadICCard("8159");
    if (result == 1) {
        var o=IdrControl1.GetICCardSN();
        if (o==0){
            $.bootstrapBox.alert.init({message:"读卡失败！"});
            return "ERROR";
        } else {
            return o;
        }
    } else {
        if (result == -1){
            $.bootstrapBox.alert.init({message:"端口初始化失败！"});
        }
        if (result == -2){
            $.bootstrapBox.alert.init({message:"读IC卡失败"});
        }
        return "ERROR";
    }
}
function jlWriteIcCard(val) {
    var result;
    //注意：参数为对应的设备端口，iDR210为8159，iDR200 USB型为1001，iDR200串口型为1至16
    result = IdrControl1.ReadICCard("8159");
    if (val instanceof Array){
        if (val.length > 0) {
            val.forEach(function (curr, index) {
                var position = curr.position;
                var data=to32(toUTF8Hex(curr.data));
                if (result==1){
                   var o= IdrControl1.WriteTypeABlock(position/4,position%4,1
                        ,"FFFFFFFFFFFF",data)
                    if (o==0){
                        $.bootstrapBox.alert.init({message:"写卡失败！"});
                        return "ERROR";
                    }
                }

            });
        }
        return true;
    }else {
        var position = val.position;
        var data=to32(toUTF8Hex(val.data));
        if (result==1){
            var o= IdrControl1.WriteTypeABlock(position/4,position%4,1
                ,"FFFFFFFFFFFF",data)
            if (o==0){
                $.bootstrapBox.alert.init({message:"写卡失败！"});
                return "ERROR";
            }
        }
        return true;
    }
}


function Picflesh() {
    document.all.notbook.src = "pic.htm";
}

function FindCPUCard() {
    document.all.oTable.rows(14).cells(1).innerText = "";
    PortOpened = IdrControl1.InitComm(1001);
    CpuCardFound = 0;
    if (PortOpened != 1) {

        IdrControl1.CloseComm();
        alert("端口初始化失败！");
        PortOpened = 0;
        return;
    }
    CpuCardFound = IdrControl1.FindICCard();
    document.all.oTable.rows(14).cells(1).innerText = "找到卡类型：" + CpuCardFound;

    if (CpuCardFound != 2)//类型2为CPU卡
    {
        IdrControl1.CloseComm();
        alert("找卡失败，请重新刷卡！");
        CpuCardFound = 0;
        return;
    }
}

function ReadSDCard() {
    document.all.oTable.rows(16).cells(1).innerText = "";
    document.all.oTable.rows(17).cells(1).innerText = "";
    document.all.oTable.rows(18).cells(1).innerText = "";
    document.all.oTable.rows(19).cells(1).innerText = "";
    document.all.oTable.rows(20).cells(1).innerText = "";

    if (CpuCardFound != 2) {
        alert("请先找CPU卡！");
        CpuCardFound = 0;
        return;
    }

    result = IdrControl1.ReadCitizenCard();

    if (result == 1) {
        document.all.oTable.rows(16).cells(1).innerText = IdrControl1.GetCityCardNO();
        document.all.oTable.rows(17).cells(1).innerText = IdrControl1.GetCityCardName();
        document.all.oTable.rows(18).cells(1).innerText = IdrControl1.GetCityCardIDType();
        document.all.oTable.rows(19).cells(1).innerText = IdrControl1.GetCityCardIDCode();
        document.all.oTable.rows(20).cells(1).innerText = IdrControl1.GetCityCardAppInfo();
    }
    else if (result == -1)
        alert("端口初始化失败！");
    if (result == -2)
        alert("找CPU卡失败！");
    if (result == -3)
        alert("读市民卡失败！");
}

function APDU_CMD() {
    if (CpuCardFound != 2) {
        alert("请先找CPU卡！");
        CpuCardFound = 0;
        return;
    }

    //alert(document.form1.apdustr.value);
    document.all.oTable.rows(22).cells(1).innerText = IdrControl1.Routon_APDU(document.form1.apdustr.value);

}

