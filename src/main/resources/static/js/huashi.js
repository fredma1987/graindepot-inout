function getVerSion() {
    clearForm();

    var CertCtl = document.getElementById("CertCtl");
    try {
        var result = CertCtl.getVersion();
        document.getElementById("result").value = result;
    } catch (e) {
    }
}

function getSamId() {
    clearForm();

    var CertCtl = document.getElementById("CertCtl");
    try {
        var result = CertCtl.getSAMID();
        document.getElementById("result").value = result;
    } catch (e) {
    }
}

function toJson(str) {
    //var obj = JSON.parse(str);
    //return obj;
    return eval('(' + str + ')');
}

function clearForm() {
    $("#sellmanname").val("");
    $("#sellmanaddress").val("");
    $("#sellmanidcard").val("");
    $("#gender").html("—&nbsp;—");
    $("#nation").html("—&nbsp;—");
    $("#bornday").html("—&nbsp;—");
    $("#certorg").html("—&nbsp;—");
    $("#effdate").html("—&nbsp;—");
    $("#expdate").html("—&nbsp;—");
    $("#photostr").attr("src", "data:image/jpeg;base64,");
}

function connect() {
    var CertCtl = document.getElementById("CertCtl");
    try {
        var result = CertCtl.connect();
    } catch (e) {
        alert(e);
    }
}

function disconnect() {
    var CertCtl = document.getElementById("CertCtl");
    try {
        var result = CertCtl.disconnect();
        document.getElementById("result").value = result;
    } catch (e) {
    }
}

function getStatus() {
    clearForm();

    var CertCtl = document.getElementById("CertCtl");
    try {
        var result = CertCtl.getStatus();
        document.getElementById("result").value = result;
    } catch (e) {
    }
}

function readCert() {
    clearForm();
    connect();
    var CertCtl = document.getElementById("CertCtl");

    try {
        var result = CertCtl.readCert();
        var resultObj = toJson(result);
        if (resultObj.resultFlag == 0) {
            $("#sellmanname").val(resultObj.resultContent.partyName);
            $("#sellmanaddress").val(resultObj.resultContent.certAddress);
            $("#sellmanidcard").val(resultObj.resultContent.certNumber);
            $("#gender").text(resultObj.resultContent.gender);
            $("#nation").text(resultObj.resultContent.nation);
            $("#bornday").text(resultObj.resultContent.bornDay);
            $("#certorg").text(resultObj.resultContent.certOrg);
            $("#effdate").text(resultObj.resultContent.effDate);
            $("#expdate").text(resultObj.resultContent.expDate);
            $("#photostr").attr("src", "data:image/jpeg;base64," + resultObj.resultContent.identityPic);
        } else {
            alert("请正确放置身份证！");
        }
    } catch (e) {
        alert(e);
    }
}

function readCarrierCert() {
    clearForm();
    connect();
    var CertCtl = document.getElementById("CertCtl");

    try {
        var result = CertCtl.readCert();
        var resultObj = toJson(result);
        if (resultObj.resultFlag == 0) {
            $("#carriername").val(resultObj.resultContent.partyName);
            $("#carrieridcard").val(resultObj.resultContent.certNumber);
        } else {
            alert("请正确放置身份证！");
        }
    } catch (e) {
        alert(e);
    }
}

function conv2base64() {
    var CertCtl = document.getElementById("CertCtl");
    try {
        var jpgPath = document.getElementById("inputJpgPath").value;
        var result;
        result = CertCtl.ConvJpgToBase64File(jpgPath);
        document.getElementById("outputBase64File").value = result;
    } catch (e) {
    }
}

function convBase64ToJpg() {
    var CertCtl = document.getElementById("CertCtl");
    try {
        var jpgPath = document.getElementById("decodeJpgPath").value;
        var base64Data = document.getElementById("base64Input").value;
        var result;
        result = CertCtl.ConvBase64ToJpg(base64Data, jpgPath);
        alert(result);
    } catch (e) {
    }
}

function download_Huashi() {
    //现在模块下增加文件，后面将放置到ftp服务器上
    window.location = "/graindepot-inout/file/huashi.exe"
}