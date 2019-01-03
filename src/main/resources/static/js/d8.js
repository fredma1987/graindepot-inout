/**
 * Created by Administrator on 2019-1-3.
 */
/**
 * 写卡
 */
function writeCart(){
    m1CardTest();
}
function m1CardTest()
{
    var rd=document.getElementById("rd");
    var st;
    var lSnr;
    var msg= "";
    /*
    功 能：初始化通讯口
    参 数：port：取值为0～19时，表示串口1～20；为100时，表示USB口通讯，此时波特率无效。
    baud：为通讯波特率9600～115200*/
    st = rd.dc_init(100, 115200);
    if(st <= 0)
    {
        msg=("初始化通讯口失败!");
        alert(msg);
        return;
    }

    /*
     说明：设置读写器将要对哪一种卡操作，读写器上电缺省的时候是对TYPEA操作
     */
    rd.dc_config_card(65);
    /**功 能：寻卡*/
     st = rd.dc_card_double(0);
    if(st != 0)
    {
        msg=("寻卡异常!");
        rd.dc_exit();
        alert(msg);
        return;
    }
    var cardid=rd.get_bstrRBuffer_asc;

    rd.put_bstrSBuffer_asc = "FFFFFFFFFFFF";
    /*功 能：将密码装入读写模块RAM中*/
    st = rd.dc_load_key(0, 0);
    if(st != 0)
    {
        msg=("密码装入异常!");
        rd.dc_exit();
        return;
    }
    /*功 能：核对密码函数*/
    st = rd.dc_authentication(0, 0);
    if(st != 0)
    {
        msg=("核对密码异常!");
        rd.dc_exit();
        return;
    }
    st=rd.dc_read(1);

    if(true){//判断卡是否绑定中
        /*后面要改成动态获取单据号*/
        var billno="2019-01-02-0001";
        var billno_buf="";
        /*转成16进制字节流*/
        for(var i=0;i<billno.length;i++){
            billno_buf+=billno.charCodeAt(i).toString(16);
        }
        rd.put_bstrSBuffer_asc = billno_buf;
        /**
         * 功 能：向卡中写入数据,参数：表示第几块，从0开始，0块为卡号
         */
        st = rd.dc_write(1);
        if(st != 0)
        {
            msg=("写卡异常!");
            rd.dc_exit();
            return;
        }
        /*后面要改成获取用户信息*/
        var userinfo="马恩恩";
        var userinfo_buf="";
        /*转成16进制字节流*/
        for(var i=0;i<userinfo.length;i++){
            userinfo_buf+=userinfo.charCodeAt(i).toString(16);
        }
        rd.put_bstrSBuffer_asc = userinfo_buf;
        /**
         * 功 能：向卡中写入数据,参数：表示第几块，从0开始，0块为卡号
         */
        st = rd.dc_write(2);
        rd.dc_read(2);
        alert(rd.get_bstrRBuffer);
        if(st != 0)
        {
            msg=("写卡异常!");
            rd.dc_exit();
            return;
        }
    }else{
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
    st = rd.dc_beep(50);
    /*功 能：关闭端口*/
    st = rd.dc_exit();
}
function encode64(input) {
    input = escape(input);
    var output = "";
    var chr1, chr2, chr3 = "";
    var enc1, enc2, enc3, enc4 = "";
    var i = 0;
    do {
        chr1 = input.charCodeAt(i++);
        chr2 = input.charCodeAt(i++);
        chr3 = input.charCodeAt(i++);
        enc1 = chr1 >> 2;
        enc2 = ((chr1 & 3) << 4) | (chr2 >> 4);
        enc3 = ((chr2 & 15) << 2) | (chr3 >> 6);
        enc4 = chr3 & 63;
        if (isNaN(chr2)) {
            enc3 = enc4 = 64;
        } else if (isNaN(chr3)) {
            enc4 = 64;
        }
        output = output +
            keyStr.charAt(enc1) +
            keyStr.charAt(enc2) +
            keyStr.charAt(enc3) +
            keyStr.charAt(enc4);
        chr1 = chr2 = chr3 = "";
        enc1 = enc2 = enc3 = enc4 = "";
    } while (i < input.length);
    return (output);
}
function decode64(input) {
    var output = "";
    var chr1, chr2, chr3 = "";
    var enc1, enc2, enc3, enc4 = "";
    var i = 0;
    // remove all characters that are not A-Z, a-z, 0-9, +, /, or =
    var base64test = /[^A-Za-z0-9\+\/\=]/g;
    if (base64test.exec(input)) {
        alert("There were invalid base64 characters in the input text.\n" +
            "Valid base64 characters are A-Z, a-z, 0-9, '+', '/', and '='\n" +
            "Expect errors in decoding.");
    }
    input = input.replace(/[^A-Za-z0-9\+\/\=]/g, "");
    do {
        enc1 = keyStr.indexOf(input.charAt(i++));
        enc2 = keyStr.indexOf(input.charAt(i++));
        enc3 = keyStr.indexOf(input.charAt(i++));
        enc4 = keyStr.indexOf(input.charAt(i++));
        chr1 = (enc1 << 2) | (enc2 >> 4);
        chr2 = ((enc2 & 15) << 4) | (enc3 >> 2);
        chr3 = ((enc3 & 3) << 6) | enc4;
        output = output + String.fromCharCode(chr1);
        if (enc3 != 64) {
            output = output + String.fromCharCode(chr2);
        }
        if (enc4 != 64) {
            output = output + String.fromCharCode(chr3);
        }
        chr1 = chr2 = chr3 = "";
        enc1 = enc2 = enc3 = enc4 = "";
    } while (i < input.length);
    return (output);
}

function typeATest()
{
    var st;
    var rlen;
    var rbuff;
    var msg = "";

    st = rd.dc_init(100, 115200);
    if(st <= 0)
    {
        msg+="dc init error";
        showMsg(msg);
        return;
    }
    msg+="dc init ok</br>";

    rd.dc_config_card(65);

    st = rd.dc_card(0, rbuff);
    if(st!=0)
    {
        msg+="dc card error </br>";
        rd.dc_exit();showMsg(msg);
        return;
    }
    msg+="dc card ok </br>";

    msg+="card id is : "+(rd.get_bstrRBuffer_asc+"</br>");

    st = rd.dc_pro_reset(rlen);
    if(st!=0)
    {
        msg+="dc pro reset error </br>";
        rd.dc_exit();showMsg(msg);
        return;
    }
    msg+="dc pro reset ok </br>";

    msg+="reset infomation : "+(rd.get_bstrRBuffer_asc+"</br>");


    rd.put_bstrSBuffer_asc = "0084000008";
    st =rd.dc_pro_command(5,rlen);
    if(st!=0)
    {
        msg+="get a random number error </br>";
        rd.dc_exit();
        showMsg(msg);
        return;
    }
    msg+="get a random number ok </br>";

    rd.dc_beep(5);

    msg+=" the random number is "+(rd.get_bstrRBuffer_asc)+ " </br>";
    showMsg(msg);
    rd.dc_exit();
}

function typeBTest()
{
    var st;
    var rlen;
    var rbuff;
    var msg = "";

    st = rd.dc_init(100, 115200);
    if(st <= 0)
    {
        msg+="dc init error";
        showMsg(msg);
        return;
    }
    msg+="dc init ok</br>";

    rd.dc_config_card(66);

    st = rd.dc_card_b();
    if(st!=0)
    {
        msg+="dc card error </br>";
        rd.dc_exit();showMsg(msg);
        return;
    }
    msg+="dc card ok </br>";

    msg+="card reset infomation "+(rd.get_bstrRBuffer_asc)+ " </br>";

    rd.put_bstrSBuffer_asc = "0084000008";
    st =rd.dc_pro_command(5,rlen);
    if(st!=0)
    {
        msg+="get a random number error </br>";
        rd.dc_exit();
        showMsg(msg);
        return;
    }
    msg+="get a random number ok </br>";

    rd.dc_beep(5);

    msg+=" the random number is "+(rd.get_bstrRBuffer_asc)+ " </br>";
    showMsg(msg);
    rd.dc_exit();

}

function M4428Test()
{
    var st;
    var rlen;
    var rbuff;
    var msg = "";

    st = rd.dc_init(100, 115200);
    if(st <= 0)
    {
        msg+="dc init error";
        showMsg(msg);
        return;
    }
    msg+="dc init ok</br>";

    st = rd.dc_setcpu(12);
    if(st != 0)
    {
        msg+="dc set cpu error"
        showmsg(msg);
        dc_exit();
        return;
    }
    msg+="dc set cpu ok</br>";

    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_read_4428(160,10);
    if(st != 0)
    {
        msg+="get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg+="get a random number ok</br>";
    msg+=" the random number is "+(rd.get_bstrRBuffer_asc)+ " </br>";

    st = rd.dc_readpincount_4428();
    if (st <= 0)
    {
        msg+="get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }

    rd.put_bstrSBuffer_asc = "ffff";
    st = rd.dc_verifypin_4428();
    if(st != 0)
    {
        msg+="get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg+="get a random number ok</br>";
    msg+=" the random number is "+(rd.get_bstrRBuffer_asc)+ " </br>";

    rd.put_bstrSBuffer_asc = "decacd1234567890ccee";
    st = rd.dc_write_4428(160,10);
    if(st != 0)
    {
        msg+="get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg+="get a random number ok</br>";
    msg+=" the random number is "+(rd.get_bstrRBuffer_asc)+ " </br>";

    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_read_4428(160,10);
    if(st != 0)
    {
        msg+="get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg+="get a random number ok</br>";
    msg+=" the random number is "+(rd.get_bstrRBuffer_asc)+ " </br>";

    rd.dc_beep(5);
    showMsg(msg);
    rd.dc_exit();

}

function M4442Test()
{
    var st;
    var rlen;
    var rbuff;
    var msg = "";

    st = rd.dc_init(100, 115200);
    if(st <= 0)
    {
        msg+="dc init error";
        showMsg(msg);
        return;
    }
    msg+="dc init ok</br>";

    st = rd.dc_setcpu(12);
    if(st != 0)
    {
        msg+="dc set cpu error"
        showmsg(msg);
        dc_exit();
        return;
    }
    msg+="dc set cpu ok</br>";

    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_read_4442(160,10);
    if(st != 0)
    {
        msg+="get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg+="get a random number ok</br>";
    msg+=" the random number is "+(rd.get_bstrRBuffer_asc)+ " </br>";

    st = rd.dc_readpincount_4442();
    if (st <= 0)
    {
        msg+="get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }

    rd.put_bstrSBuffer_asc = "ffffff";
    st = rd.dc_verifypin_4442();
    if(st != 0)
    {
        msg+="get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg+="get a random number ok</br>";
    msg+=" the random number is "+(rd.get_bstrRBuffer_asc)+ " </br>";

    rd.put_bstrSBuffer_asc = "decacd1234567890ccee";
    st = rd.dc_write_4442(160,10);
    if(st != 0)
    {
        msg+="get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg+="get a random number ok</br>";
    msg+=" the random number is "+(rd.get_bstrRBuffer_asc)+ " </br>";

    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_read_4442(160,10);
    if(st != 0)
    {
        msg+="get a random number error";
        showMsg(msg)
        rd.dc_exit();
        return;
    }
    msg+="get a random number ok</br>";
    msg+=" the random number is "+(rd.get_bstrRBuffer_asc)+ " </br>";

    rd.dc_beep(5);
    showMsg(msg);
    rd.dc_exit();

}

function contactCpuTest()
{
    var st;
    var rlen;
    var rbuff;
    var msg = "";

    st = rd.dc_init(100, 115200);
    if(st <= 0)
    {
        msg+="dc init error";
        showMsg(msg);
        return;
    }
    msg+="dc init ok</br>";

    st = rd.dc_setcpu(12);
    if(st != 0)
    {
        msg+="dc set cpu error"
        showmsg(msg);
        dc_exit();
        return;
    }
    msg+="dc set cpu ok</br>";

    st = rd.dc_cpureset(rlen);
    if(st != 0)
    {
        msg+="dc cpu reset error";
        showMsg(msg)
        dc_exit();
        return;
    }
    msg+="dc cpu reset ok</br>";

    msg+="reset infomation : "+(rd.get_bstrRBuffer_asc+"</br>");

    rd.put_bstrSBuffer_asc = "0084000008";
    st = rd.dc_cpuapdu(5,rlen);
    if(st != 0)
    {
        msg+="get a random number error";
        showMsg(msg)
        dc_exit();
        return;
    }
    msg+="get a random number ok</br>";
    msg+=" the random number is "+(rd.get_bstrRBuffer_asc)+ " </br>";


    rd.dc_beep(5);
    showMsg(msg);
    rd.dc_exit();

}


function idCardTest()
{

    var st; //主要用于返回值
    var lSnr; //本用于取序列号，但在javascript只是当成dc_card函数的一个临时变量
    var rlen; //用于取一些返回值长度，但在javascript只是当成dc_card函数的一个临时变量
    var i,m,n;
    var msg = "";



    st = rd.dc_init(100, 115200);
    if(st <= 0)
    {
        msg+="dc init error";
        showMsg(msg);
        return;
    }
    msg+="dc init ok</br>";

    //******************  身份证  **************************
    //alert("开始读取身份证数据!");
    //rd.DC_find_i_d();
    st = rd.DC_start_i_d();
    if (st < 0)
    {
        msg+="读取身份证信息失败";
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
    var st=rd.DC_i_d_query_photo_bmp_buffer();
    var bmp_data_str=rd.get_bstrRBuffer_asc;
    rd.put_bstrSBuffer = "c:/me.bmp";
    st = rd.DC_i_d_query_photo_file();


    rd.DC_end_i_d();

    msg += "姓名 ： "+ name + "</br>"
        +"性别 ： "+ sex + "</br>"
        +"民族 ： "+ nation + "</br>"
        +"生日 ： "+ birth + "</br>"
        +"地址 ： "+ address +"</br>"
        +"身份证号 ： "+ number +"</br>"
        +"身份证签发机关 : "+department +"</br>"
        +"有效期 ： "+ expire +"</br>"
        +"照片 bmp 文件十六进制数据的显示表示：" +  bmp_data_str + "</br>"
        +'<img src="c:/me.bmp" alt="me">';
    showMsg(msg);
}


function typeATest()
{//typeA非接触式cpu卡测试

    var rlen,mrandom,resetbuff,msg = "";
    rd.dc_reset();//复位射频

    result = rd.dc_config_card(65);//配置为A型卡片
    if(result < 0)
    {
        return;
    }

    result = rd.dc_card_pro(rlen);//寻卡
    if(result < 0)
    {
        msg += "寻卡失败";
        showMsg(msg);
        return;
    }
    msg+="寻卡成功</br>";

    result =rd. dc_pro_reset(rlen);//复位卡片
    if(result < 0)
    {
        msg += "复位失败</br>"
        showMsg(msg);
        return;
    }
    resetbuff = rd.get_bstrRBuffer_asc;//获取复位信息
    msg += ("复位成功" + resetbuff)+"</br>";


    rd.put_bstrSBuffer_asc = "0084000008";//传送需要发送的指令数据
    result = rd.dc_pro_commandlink (5,rlen, 10,56);//发送指令
    if(result < 0)
    {
        msg += "发送指令失败</br>";
        showMsg(msg);
        return;
    }
    mrandom = rd.get_bstrRBuffer_asc ;
    msg += "取随机数成功，</br>随机数为 ："+ mrandom+"</br>";
    showMsg(msg);


}

function showMsg(msg)
{
    x=document.getElementById("demo");
    x.innerHTML=msg;
}

function mInit()
{
    rd.dc_init(100, 115200);
}

function mBeep()
{
    rd.dc_beep(2);
}

function mExit()
{
    rd.dc_exit();
}