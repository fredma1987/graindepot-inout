//ic卡读卡器对象  type:1 精伦读卡器
function IdFactory(type) {
    this.type=type;
    this.createIdObject=function () {
        var factory;
        switch (this.type) {
            case "1":
                factory = new JingLunId();
                break;
            default :
                factory = new JingLunId();
        }
        return factory;
    }
}

function JingLunId() {
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
    this.readIdCard=function () {
        var result;
        var obj={};
        //注意：第一个参数为对应的设备端口，USB型为1001，串口型为1至16
        //result=IdrControl1.ReadCard("1001","c:\\test.jpg");
        result=IdrControl1.ReadCard("1001","");
        if (result==1){
            obj.name=IdrControl1.GetName();
            obj.idnumber=IdrControl1.GetCode();
            obj.nation=IdrControl1.GetFolk();
            obj.sex=IdrControl1.GetSex();
            obj.birthday=IdrControl1.GetBirthYear() + "," + IdrControl1.GetBirthMonth() + "," + IdrControl1.GetBirthDay() ;
            obj.address=IdrControl1.GetAddress();
            obj.agency=IdrControl1.GetAgency();
            obj.valid=IdrControl1.GetValid();
            obj.picBase64=IdrControl1.GetPhotobuf();
        }else{
            if (result==-1)
                $.bootstrapBox.alert.init({message:"端口初始化失败！"});
            if (result==-2)
                $.bootstrapBox.alert.init({message:"请重新将卡片放到读卡器上！"});
            if (result==-3)
                $.bootstrapBox.alert.init({message:"读取数据失败！"});
            if (result==-4)
                $.bootstrapBox.alert.init({message:"生成照片文件失败，请检查设定路径和磁盘空间！"});

            return false
        }
        return obj;
    };
}