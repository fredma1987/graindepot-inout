//ic卡读卡器对象  type:1 精伦读卡器 2:德卡d8读卡器
function IcFactory(type) {
    this.type=type;
    this.createIcObject=function () {
        var factory;
        switch (this.type+"") {
            case "1":
                factory = new JingLun();
                break;
            case "2":
                factory = new d8();
                break;
            default :
                factory = new JingLun();
        }
        return factory;
    }
}

function JingLun() {
    //用于打开串口并读取卡内信息
    this.init=function () {
        var result;
        //注意：参数为对应的设备端口，iDR210为8159，iDR200 USB型为1001，iDR200串口型为1至16
        result = IdrControl1.ReadICCard("8159");
        if (result==1) {
            return true
        }else {
            if (result == -1){
                $.bootstrapBox.alert.init({message:"端口初始化失败！"});
            }
            if (result == -2){
                $.bootstrapBox.alert.init({message:"读IC卡失败"});
            }
            return false;
        }
    };
    this.readCard=function (position) {
        if (this.init()){
            var o=IdrControl1.ReadTypeABlock(position/4,position%4,1,"FFFFFFFFFFFF");
            if (o+""=='0') {
                return "ERROR";
            }else {
                var real=o.replaceAll("00","");
                return utf8HexToStr(real);
            }
        } else {
            return "ERROR";
        }
    };
    this.readCardNum=function (position) {
        if (this.init()){
            var o=IdrControl1.GetICCardSN();
            if (o+""=='0') {
                return "ERROR";
            }else {
                return o.substring(0,8);
            }
        } else {
            return "ERROR";
        }
    };
    this.readCardAll=function () {
        if (this.init()){
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
        }else {
            return "ERROR";
        }
    };
    this.writeCard=function (val) {
        if (this.init()){
            if (val instanceof Array){
                if (val.length > 0) {
                    val.forEach(function (curr, index) {
                        var position = curr.position;
                        var data=to32(toUTF8Hex(curr.data));
                        var o= IdrControl1.WriteTypeABlock(position/4,position%4,1
                            ,"FFFFFFFFFFFF",data)
                        if (o+""=='0'){
                            $.bootstrapBox.alert.init({message:"写卡失败！"});
                            return false;
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
                    if (o+""=='0'){
                        $.bootstrapBox.alert.init({message:"写卡失败！"});
                        return false;
                    }
                }
                return true;
            }

        }else {
            return false;
        }
    }
}

function d8() {
    //初始化
    function initD8(rd) {
        /*
      功 能：初始化通讯口
      参 数：port：取值为0～19时，表示串口1～20；为100时，表示USB口通讯，此时波特率无效。
      baud：为通讯波特率9600～115200*/
        var st = rd.dc_init(100, 115200);
        if (st <= 0) {
            var msg = "初始化通讯口失败,请重新接入读卡器";
            $.bootstrapBox.alert.init({message: msg});
            return false;
        } else {
            return true;
        }
    }

    //寻卡
    function getCard(rd) {
        /*
         说明：设置读写器将要对哪一种卡操作，读写器上电缺省的时候是对TYPEA操作
         */
        rd.dc_config_card(65);
        /**功 能：寻卡*/
        var st = rd.dc_card_double(0);
        if (st != 0) {
            var msg = "寻卡异常,请将ic卡摆放至读卡器上";
            rd.dc_exit();
            $.bootstrapBox.alert.init({message: msg});
            return false;
        } else {
            return true;
        }
    }

    //密码装入 num:扇区的坐标  从0开始
    function initPWD(rd, num) {
        /*在第一个扇区读写*/
        rd.put_bstrSBuffer_asc = "FFFFFFFFFFFF";
        /*功 能：将密码装入读写模块RAM中 ，第二个参数指扇区*/
        var st = rd.dc_load_key(0, num);
        if (st != 0) {
            var msg = "密码装入异常!";
            rd.dc_exit();
            $.bootstrapBox.alert.init({message: msg});
            return false;
        } else {
            return true
        }
    }

    //核对密码
    function checkPWD(rd, num) {
        /*功 能：核对密码函数，，第二个参数指扇区*/
        var st = rd.dc_authentication(0, num);
        if (st != 0) {
            var msg = "核对密码异常!";
            rd.dc_exit();
            $.bootstrapBox.alert.init({message: msg});
            return false;
        } else {
            return true
        }
    }
    function getFanNum(position) {
        return parseInt(position / 4)
    }
    //写卡
    function doWriteCard(rd, position, data) {
        /**
         * 功 能：向卡中写入数据,参数：表示第几块，从0开始，0块为卡号
         */
            //rd.put_bstrSBuffer = data;
        var hexData = to32(toUTF8Hex(data));
        rd.put_bstrSBuffer_asc = hexData;
        var st = rd.dc_write(position);
        if (st != 0) {
            var msg = "写卡异常!";
            rd.dc_exit();
            $.bootstrapBox.alert.init({message: msg});
            return false;
        } else {
            return true
        }

    }
    //用于打开串口并读取卡内信息
    this.init=function () {

    };
    this.readCard=function (position) {
        var rd = document.getElementById("rd");
        var fanNum = getFanNum(position);
        if (initD8(rd) && getCard(rd) && initPWD(rd, fanNum) && checkPWD(rd, fanNum)) {
            var st = rd.dc_read(position);
            var result = rd.get_bstrRBuffer_asc;
            var va = utf8HexToStr(result.replaceAll("00",""));
            rd.dc_beep(20);
            rd.dc_beep(20);
            rd.dc_exit();
            return va;
        } else {
            rd.dc_exit();
            return "ERROR";
        }
    };
    this.readCardNum=function (position) {
        var rd = document.getElementById("rd");
        var position = 0;
        var fanNum = getFanNum(position);
        var result;
        if (initD8(rd) && getCard(rd) && initPWD(rd, fanNum) && checkPWD(rd, fanNum)) {
            var st = rd.dc_read(position);
            result = rd.get_bstrRBuffer_asc;
            rd.dc_exit();
            return result.substring(0,8);
        } else {
            rd.dc_exit();
            return false;
        }
    };
    this.readCardAll=function () {
        var rd = document.getElementById("rd");
        var results = [];
        for (var i = 0; i <= 15; i++) {
            for (var j = 0; j <= 2; j++) {
                if ((i + 1) % 4 != 0) {
                    var position = i * 4 + j;
                    var fanNum = getFanNum(position);
                    if (initD8(rd) && getCard(rd) && initPWD(rd, fanNum) && checkPWD(rd, fanNum)) {
                        var st = rd.dc_read(position);
                        var o = rd.get_bstrRBuffer_asc;
                        var va = utf8HexToStr(o.replaceAll("00",""));
                        results.push({
                            index: i + "_" + j,
                            data: va
                        });
                        rd.dc_exit();
                    }else {
                        rd.dc_exit();
                        return false;
                    }
                }
            }
        }
        rd.dc_beep(20);
        rd.dc_beep(20);
        return results;
    };
    this.writeCard=function (val) {
        var rd =document.getElementById("rd");
        if (initD8(rd) && getCard(rd)) {
            if (val.length > 0) {
                val.forEach(function (curr, index) {
                    var position = curr.position;
                    var fanNum = getFanNum(position);
                    if (initPWD(rd, fanNum) && checkPWD(rd, fanNum) && curr.data != null) {
                        var success = doWriteCard(rd, position, curr.data);
                        if (!success) {
                            rd.dc_exit();
                            return false;
                        }
                    } else {
                        rd.dc_exit();
                        return false;
                    }

                });
                rd.dc_beep(20);
                rd.dc_beep(20);
                rd.dc_exit();
                return true;
            } else {
                rd.dc_exit();
                return false;
            }
        } else {
            rd.dc_exit();
            return false;
        }
    }
}