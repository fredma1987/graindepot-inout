    function getVerSion()
    {
        clearForm();

        var CertCtl = document.getElementById("CertCtl");
        try
        {
            var result = CertCtl.getVersion();
            document.getElementById("result").value = result;
        } catch (e)
        {
        }
    }
function getSamId()
{
    clearForm();

    var CertCtl = document.getElementById("CertCtl");
    try
    {
        var result = CertCtl.getSAMID();
        document.getElementById("result").value = result;
    } catch (e)
    {
    }
}
function toJson(str)
{
    //var obj = JSON.parse(str);
    //return obj;
    return eval('('+str+')');
}
function clearForm()
{
    document.getElementById("partyName").value = "";
    document.getElementById("certNumber").value = "";
    document.getElementById("certAddress").value = "";
}
function connect()
{
    var CertCtl = document.getElementById("CertCtl");
    try {
        var result = CertCtl.connect();
        document.getElementById("result").value = result;
    } catch (e)
    {
    }
}
function disconnect()
{
    var CertCtl = document.getElementById("CertCtl");
    try
    {
        var result = CertCtl.disconnect();
        document.getElementById("result").value = result;
    } catch (e)
    {
    }
}
function getStatus()
{
    clearForm();

    var CertCtl = document.getElementById("CertCtl");
    try {
        var result = CertCtl.getStatus();
        document.getElementById("result").value = result;
    } catch(e) {
    }
}
function readCert()
{
    clearForm();
    connect();
    var CertCtl = document.getElementById("CertCtl");

    try {
        var result = CertCtl.readCert();
        var resultObj = toJson(result);
        if (resultObj.resultFlag == 0)
        {
            $("#partyName").val(resultObj.resultContent.partyName);
            $("#certAddress").val(resultObj.resultContent.certAddress);
            $("#certNumber").val(resultObj.resultContent.certNumber);
            $("#gender").text(resultObj.resultContent.gender);
            $("#nation").text(resultObj.resultContent.nation);
            $("#bornDay").text(resultObj.resultContent.bornDay);
            $("#certOrg").text(resultObj.resultContent.certOrg);
            $("#effDate").text(resultObj.resultContent.effDate);
            $("#expDate").text(resultObj.resultContent.expDate);
            $("#PhotoStr").attr("src","data:image/jpeg;base64,"+ resultObj.resultContent.identityPic);
        }
    } catch(e)
    {
        alert(e);
    }
}
function conv2base64()
{
    var CertCtl = document.getElementById("CertCtl");
    try
    {
        var jpgPath = document.getElementById("inputJpgPath").value;
        var result;
        result = CertCtl.ConvJpgToBase64File(jpgPath);
        document.getElementById("outputBase64File").value = result;
    } catch (e)
    {
    }
}

function convBase64ToJpg()
{
    var CertCtl = document.getElementById("CertCtl");
    try
    {
        var jpgPath = document.getElementById("decodeJpgPath").value;
        var base64Data = document.getElementById("base64Input").value;
        var result;
        result = CertCtl.ConvBase64ToJpg(base64Data, jpgPath);
        alert(result);
    } catch (e) {
    }
}