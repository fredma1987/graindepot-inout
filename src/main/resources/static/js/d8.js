/**
 * Created by Administrator on 2019-1-3.
 */
/**
 * 写卡
 */
function writeCart() {
    m1CardTest_bak();
}

function m1CardTest_bak() {
    var rd = document.getElementById("rd");
    var st;
    var lSnr;
    var msg = "";
    /*
    功 能：初始化通讯口
    参 数：port：取值为0～19时，表示串口1～20；为100时，表示USB口通讯，此时波特率无效。
    baud：为通讯波特率9600～115200*/
    st = rd.dc_init(100, 115200);
    if (st <= 0) {
        msg = "初始化通讯口失败!";
        alert(msg);
        return;
    }

    /*
     说明：设置读写器将要对哪一种卡操作，读写器上电缺省的时候是对TYPEA操作
     */
    rd.dc_config_card(65);
    /**功 能：寻卡*/
    st = rd.dc_card_double(0);
    if (st != 0) {
        msg = "寻卡异常!";
        rd.dc_exit();
        alert(msg);
        return;
    }
    var cardid = rd.get_bstrRBuffer_asc;
    /*在第一个扇区读写*/
    rd.put_bstrSBuffer_asc = "FFFFFFFFFFFF";
    /*功 能：将密码装入读写模块RAM中 ，第二个参数指扇区*/
    st = rd.dc_load_key(0, 0);
    if (st != 0) {
        msg = "密码装入异常!";
        rd.dc_exit();
        alert(msg);
        return;
    }
    /*功 能：核对密码函数，，第二个参数指扇区*/
    st = rd.dc_authentication(0, 0);
    if (st != 0) {
        msg = "核对密码异常!";
        rd.dc_exit();
        alert(msg);
        return;
    }
    st = rd.dc_read(1);

    if (true) {//判断卡是否绑定中
        /*后面要改成动态获取单据号*/
        var billno = "2019-01-02-0001";
        rd.put_bstrSBuffer = billno;
        /**
         * 功 能：向卡中写入数据,参数：表示第几块，从0开始，0块为卡号
         */
        st = rd.dc_write(1);
        if (st != 0) {
            msg = "写卡异常!";
            rd.dc_exit();
            alert(msg);
            return;
        }
        /*后面要改成获取用户信息*/
        var userinfo = "马恩恩";
        rd.put_bstrSBuffer = userinfo;
        /**
         * 功 能：向卡中写入数据,参数：表示第几块，从0开始，0块为卡号
         */
        try {
            st = rd.dc_write(2);
        } catch (e) {
            alert(e);
            rd.dc_exit();
            return;
        }
        if (st != 0) {
            msg = "写卡异常!";
            rd.dc_exit();
            alert(msg);
            return;
        }

        /*在第二个扇区读写*/
        rd.put_bstrSBuffer_asc = "FFFFFFFFFFFF";
        /*功 能：将密码装入读写模块RAM中，，第二个参数指扇区*/
        st = rd.dc_load_key(0, 1);
        if (st != 0) {
            msg = "密码装入异常!";
            rd.dc_exit();
            alert(msg);
            return;
        }
        /*功 能：核对密码函数，，第二个参数指扇区*/
        st = rd.dc_authentication(0, 1);
        if (st != 0) {
            msg = "核对密码异常!";
            rd.dc_exit();
            alert(msg);
            return;
        }

        /*后面要改成获取车辆信息*/
        var carinfo = "苏BA3G19|";
        rd.put_bstrSBuffer = carinfo;
        /**
         * 功 能：向卡中写入数据,参数：表示第几块，从0开始，0块为卡号
         */
        try {
            st = rd.dc_write(4);
        } catch (e) {
            alert(e);
            rd.dc_exit();
            return;
        }
        if (st != 0) {
            msg = "写卡异常!";
            rd.dc_exit();
            alert(msg);
            return;
        }
        /*后面要改成获取身份证信息*/
        var idinfo = "320722198704035496";
        rd.put_bstrSBuffer = idinfo.substr(0, 16);
        /**
         * 功 能：向卡中写入数据,参数：表示第几块，从0开始，0块为卡号
         */
        try {
            st = rd.dc_write(5);
            rd.put_bstrSBuffer = idinfo.substr(16, 18) + "|";
            st = rd.dc_write(6);
        } catch (e) {
            alert(e);
            rd.dc_exit();
            return;
        }
        if (st != 0) {
            msg = "写卡异常!";
            rd.dc_exit();
            alert(msg);
            return;
        }
    } else {
        rd.dc_exit();
        alert("该卡已经被绑定！");
        return;
    }

    /*功 能：读取卡中数据 ,参数：表示第几块，从0开始，0块为卡号*/
    //st = rd.dc_read(1);

    /*取字符串*/
    //rd.get_bstrRBuffer;

    /*取16进制字节流*/
    //rd.get_bstrRBuffer_asc;

    /*功 能：蜂鸣 ,参数：蜂鸣时间 单位毫秒*/
    st = rd.dc_beep(20);
    st = rd.dc_beep(20);
    /*功 能：关闭端口*/
    st = rd.dc_exit();
}

function stringToByte(str) {
    var bytes = new Array();
    var len, c;
    len = str.length;
    for (var i = 0; i < len; i++) {
        c = str.charCodeAt(i);
        if (c >= 0x010000 && c <= 0x10FFFF) {
            bytes.push(((c >> 18) & 0x07) | 0xF0);
            bytes.push(((c >> 12) & 0x3F) | 0x80);
            bytes.push(((c >> 6) & 0x3F) | 0x80);
            bytes.push((c & 0x3F) | 0x80);
        } else if (c >= 0x000800 && c <= 0x00FFFF) {
            bytes.push(((c >> 12) & 0x0F) | 0xE0);
            bytes.push(((c >> 6) & 0x3F) | 0x80);
            bytes.push((c & 0x3F) | 0x80);
        } else if (c >= 0x000080 && c <= 0x0007FF) {
            bytes.push(((c >> 6) & 0x1F) | 0xC0);
            bytes.push((c & 0x3F) | 0x80);
        } else {
            bytes.push(c & 0xFF);
        }
    }
    return bytes;


}

function typeATest() {
    var st;
    var rlen;
    var rbuff;
    var msg = "";

    st = rd.dc_init(100, 115200);
    if (st <= 0) {
        msg += "dc init error";
        showMsg(msg);
        return;
    }
    msg += "dc init ok</br>";

    rd.dc_config_card(65);

    st = rd.dc_card(0, rbuff);
    if (st != 0) {
        msg += "dc card error </br>";
        rd.dc_exit();
        showMsg(msg);
        return;
    }
    msg += "dc card ok </br>";

    msg += "card id is : " + (rd.get_bstrRBuffer_asc + "</br>");

    st = rd.dc_pro_reset(rlen);
    if (st != 0) {
        msg += "dc pro reset error </br>";
        rd.dc_exit();
        showMsg(msg);
        return;
    }
    msg += "dc pro reset ok </br>";

    msg += "reset infomation : " + (rd.get_bstrRBuffer_asc + "</br>");


    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_pro_command(5, rlen);
    if (st != 0) {
        msg += "get a random number error </br>";
        rd.dc_exit();
        showMsg(msg);
        return;
    }
    msg += "get a random number ok </br>";

    rd.dc_beep(5);

    msg += " the random number is " + (rd.get_bstrRBuffer_asc) + " </br>";
    showMsg(msg);
    rd.dc_exit();
}

function typeBTest() {
    var st;
    var rlen;
    var rbuff;
    var msg = "";

    st = rd.dc_init(100, 115200);
    if (st <= 0) {
        msg += "dc init error";
        showMsg(msg);
        return;
    }
    msg += "dc init ok</br>";

    rd.dc_config_card(66);

    st = rd.dc_card_b();
    if (st != 0) {
        msg += "dc card error </br>";
        rd.dc_exit();
        showMsg(msg);
        return;
    }
    msg += "dc card ok </br>";

    msg += "card reset infomation " + (rd.get_bstrRBuffer_asc) + " </br>";

    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_pro_command(5, rlen);
    if (st != 0) {
        msg += "get a random number error </br>";
        rd.dc_exit();
        showMsg(msg);
        return;
    }
    msg += "get a random number ok </br>";

    rd.dc_beep(5);

    msg += " the random number is " + (rd.get_bstrRBuffer_asc) + " </br>";
    showMsg(msg);
    rd.dc_exit();

}

function M4428Test() {
    var st;
    var rlen;
    var rbuff;
    var msg = "";

    st = rd.dc_init(100, 115200);
    if (st <= 0) {
        msg += "dc init error";
        showMsg(msg);
        return;
    }
    msg += "dc init ok</br>";

    st = rd.dc_setcpu(12);
    if (st != 0) {
        msg += "dc set cpu error"
        showmsg(msg);
        dc_exit();
        return;
    }
    msg += "dc set cpu ok</br>";

    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_read_4428(160, 10);
    if (st != 0) {
        msg += "get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg += "get a random number ok</br>";
    msg += " the random number is " + (rd.get_bstrRBuffer_asc) + " </br>";

    st = rd.dc_readpincount_4428();
    if (st <= 0) {
        msg += "get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }

    rd.put_bstrSBuffer_asc = "ffff";
    st = rd.dc_verifypin_4428();
    if (st != 0) {
        msg += "get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg += "get a random number ok</br>";
    msg += " the random number is " + (rd.get_bstrRBuffer_asc) + " </br>";

    rd.put_bstrSBuffer_asc = "decacd1234567890ccee";
    st = rd.dc_write_4428(160, 10);
    if (st != 0) {
        msg += "get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg += "get a random number ok</br>";
    msg += " the random number is " + (rd.get_bstrRBuffer_asc) + " </br>";

    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_read_4428(160, 10);
    if (st != 0) {
        msg += "get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg += "get a random number ok</br>";
    msg += " the random number is " + (rd.get_bstrRBuffer_asc) + " </br>";

    rd.dc_beep(5);
    showMsg(msg);
    rd.dc_exit();

}

function M4442Test() {
    var st;
    var rlen;
    var rbuff;
    var msg = "";

    st = rd.dc_init(100, 115200);
    if (st <= 0) {
        msg += "dc init error";
        showMsg(msg);
        return;
    }
    msg += "dc init ok</br>";

    st = rd.dc_setcpu(12);
    if (st != 0) {
        msg += "dc set cpu error"
        showmsg(msg);
        dc_exit();
        return;
    }
    msg += "dc set cpu ok</br>";

    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_read_4442(160, 10);
    if (st != 0) {
        msg += "get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg += "get a random number ok</br>";
    msg += " the random number is " + (rd.get_bstrRBuffer_asc) + " </br>";

    st = rd.dc_readpincount_4442();
    if (st <= 0) {
        msg += "get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }

    rd.put_bstrSBuffer_asc = "ffffff";
    st = rd.dc_verifypin_4442();
    if (st != 0) {
        msg += "get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg += "get a random number ok</br>";
    msg += " the random number is " + (rd.get_bstrRBuffer_asc) + " </br>";

    rd.put_bstrSBuffer_asc = "decacd1234567890ccee";
    st = rd.dc_write_4442(160, 10);
    if (st != 0) {
        msg += "get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg += "get a random number ok</br>";
    msg += " the random number is " + (rd.get_bstrRBuffer_asc) + " </br>";

    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_read_4442(160, 10);
    if (st != 0) {
        msg += "get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg += "get a random number ok</br>";
    msg += " the random number is " + (rd.get_bstrRBuffer_asc) + " </br>";

    rd.dc_beep(5);
    showMsg(msg);
    rd.dc_exit();

}

function contactCpuTest() {
    var st;
    var rlen;
    var rbuff;
    var msg = "";

    st = rd.dc_init(100, 115200);
    if (st <= 0) {
        msg += "dc init error";
        showMsg(msg);
        return;
    }
    msg += "dc init ok</br>";

    st = rd.dc_setcpu(12);
    if (st != 0) {
        msg += "dc set cpu error"
        showmsg(msg);
        dc_exit();
        return;
    }
    msg += "dc set cpu ok</br>";

    st = rd.dc_cpureset(rlen);
    if (st != 0) {
        msg += "dc cpu reset error";
        showMsg(msg)
        dc_exit();
        return;
    }
    msg += "dc cpu reset ok</br>";

    msg += "reset infomation : " + (rd.get_bstrRBuffer_asc + "</br>");

    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_cpuapdu(5, rlen);
    if (st != 0) {
        msg += "get a random number error";
        showMsg(msg)
        dc_exit();
        return;
    }
    msg += "get a random number ok</br>";
    msg += " the random number is " + (rd.get_bstrRBuffer_asc) + " </br>";


    rd.dc_beep(5);
    showMsg(msg);
    rd.dc_exit();

}


function idCardTest() {

    var st; //主要用于返回值
    var lSnr; //本用于取序列号，但在javascript只是当成dc_card函数的一个临时变量
    var rlen; //用于取一些返回值长度，但在javascript只是当成dc_card函数的一个临时变量
    var i, m, n;
    var msg = "";


    st = rd.dc_init(100, 115200);
    if (st <= 0) {
        msg += "dc init error";
        showMsg(msg);
        return;
    }
    msg += "dc init ok</br>";

    //******************  身份证  **************************
    //alert("开始读取身份证数据!");
    //rd.DC_find_i_d();
    st = rd.DC_start_i_d();
    if (st < 0) {
        msg += "读取身份证信息失败";
        showMsg(msg);
        return;
    }
    var name = rd.DC_i_d_query_name();
    var sex = rd.DC_i_d_query_sex();
    var nation = rd.DC_i_d_query_nation();
    var birth = rd.DC_i_d_query_birth();
    var address = rd.DC_i_d_query_address();
    var number = rd.DC_i_d_query_id_number();
    var department = rd.DC_i_d_query_department();
    var expire = rd.DC_i_d_query_expire_day();
    var st = rd.DC_i_d_query_photo_bmp_buffer();
    var bmp_data_str = rd.get_bstrRBuffer_asc;
    rd.put_bstrSBuffer = "c:/me.bmp";
    st = rd.DC_i_d_query_photo_file();


    rd.DC_end_i_d();

    msg += "姓名 ： " + name + "</br>"
        + "性别 ： " + sex + "</br>"
        + "民族 ： " + nation + "</br>"
        + "生日 ： " + birth + "</br>"
        + "地址 ： " + address + "</br>"
        + "身份证号 ： " + number + "</br>"
        + "身份证签发机关 : " + department + "</br>"
        + "有效期 ： " + expire + "</br>"
        + "照片 bmp 文件十六进制数据的显示表示：" + bmp_data_str + "</br>"
        + '<img src="c:/me.bmp" alt="me">';
    showMsg(msg);
}


function typeATest() {//typeA非接触式cpu卡测试

    var rlen, mrandom, resetbuff, msg = "";
    rd.dc_reset();//复位射频

    result = rd.dc_config_card(65);//配置为A型卡片
    if (result < 0) {
        return;
    }

    result = rd.dc_card_pro(rlen);//寻卡
    if (result < 0) {
        msg += "寻卡失败";
        showMsg(msg);
        return;
    }
    msg += "寻卡成功</br>";

    result = rd.dc_pro_reset(rlen);//复位卡片
    if (result < 0) {
        msg += "复位失败</br>"
        showMsg(msg);
        return;
    }
    resetbuff = rd.get_bstrRBuffer_asc;//获取复位信息
    msg += ("复位成功" + resetbuff) + "</br>";


    rd.put_bstrSBuffer_asc = "0084000008";//传送需要发送的指令数据
    result = rd.dc_pro_commandlink(5, rlen, 10, 56);//发送指令
    if (result < 0) {
        msg += "发送指令失败</br>";
        showMsg(msg);
        return;
    }
    mrandom = rd.get_bstrRBuffer_asc;
    msg += "取随机数成功，</br>随机数为 ：" + mrandom + "</br>";
    showMsg(msg);


}

function showMsg(msg) {
    x = document.getElementById("demo");
    x.innerHTML = msg;
}

function mInit() {
    rd.dc_init(100, 115200);
}

function mBeep() {
    rd.dc_beep(2);
}

function mExit() {
    rd.dc_exit();
}

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

//写卡
function doWriteCard(rd, position, data) {
    /**
     * 功 能：向卡中写入数据,参数：表示第几块，从0开始，0块为卡号
     */
        //rd.put_bstrSBuffer = data;
    var hexData = toUTF8Hex(data);
    if (hexData.length <= 30) {
        hexData += '21'//加入!分隔
    }
    // rd.put_bstrSBuffer_asc = data;
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

function getFanNum(position) {
    return parseInt(position / 4)
}

function d8WriteCard(data, rd) {
    var rd = rd ? rd : document.getElementById("rd");
    if (initD8(rd) && getCard(rd)) {
        if (data.length > 0) {
            data.forEach(function (curr, index) {
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

//读卡 读卡失败返回 'ERROR'
function d8ReadCard(position, rd) {
    var rd = rd ? rd : document.getElementById("rd");
    var fanNum = getFanNum(position);
    if (initD8(rd) && getCard(rd) && initPWD(rd, fanNum) && checkPWD(rd, fanNum)) {
        var st = rd.dc_read(position);
        //var result = rd.get_bstrRBuffer;
        //var result = rd.get_bstrRBuffer_asc;
        console.log(rd.get_bstrRBuffer);
        console.log(rd.get_bstrRBuffer_asc);
        var va = utf8HexToStr(rd.get_bstrRBuffer_asc);
        var result=null;
        if (va != null) {
            result = va.split("!")[0];
        }
        rd.dc_beep(20);
        rd.dc_beep(20);
        rd.dc_exit();
        return result;
    } else {
        rd.dc_exit();
        return "ERROR";
    }
}

//读卡全部
function d8ReadCardAll(rd) {
    var rd = rd ? rd : document.getElementById("rd");
    var results = [];
    for (var i = 0; i <= 15; i++) {
        for (var j = 0; j <= 2; j++) {
            if ((i + 1) % 4 != 0) {
                var position = i * 4 + j;
                var fanNum = getFanNum(position);
                if (initD8(rd) && getCard(rd) && initPWD(rd, fanNum) && checkPWD(rd, fanNum)) {
                    var st = rd.dc_read(position);
                    //var result = rd.get_bstrRBuffer;
                    var va = utf8HexToStr(rd.get_bstrRBuffer_asc);
                    var result;
                    if (va != null) {
                        result = va.split("!")[0];
                    }
                    results.push({
                        index: i + "_" + j,
                        data: result
                    });
                    rd.dc_exit();
                }
            }
        }
    }
    rd.dc_beep(20);
    rd.dc_beep(20);
    return results;
}

//读取ic卡号
function getIcNumber(rd) {
    var rd = rd ? rd : document.getElementById("rd");
    var position = 0;
    var fanNum = getFanNum(position);
    var result;
    if (initD8(rd) && getCard(rd) && initPWD(rd, fanNum) && checkPWD(rd, fanNum)) {
        var st = rd.dc_read(position);
        result = rd.get_bstrRBuffer_asc;
        rd.dc_exit();
        return result;
    } else {
        rd.dc_exit();
        return false;
    }
}


