//ic卡读卡器对象  type:1 精伦读卡器
function IcFactory(type) {
    this.type=type;
    this.createIcObject=function () {
        var factory;
        switch (this.type) {
            case "1":
                factory = new JingLun();
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
            if (o==0) {
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
            if (o==0) {
                return "ERROR";
            }else {
                return o;
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
                        if (o==0){
                            $.bootstrapBox.alert.init({message:"写卡失败！"});
                            return "ERROR";
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

        }else {
            return "ERROR";
        }
    }
}