package org.wso2.bench;

public class HardcodedResponse {

    public static StringBuilder RESPONSE = new StringBuilder("<?xml version='1.0' encoding='UTF-8'?>\n" +
            "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
            "    <soapenv:Header/>\n" +
            "    <soapenv:Body>\n" +
            "        <m0:getQuote xmlns:m0=\"http://services.samples\">\n" +
            "            <m0:request>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "                <m0:symbol>WSO2</m0:symbol>\n" +
            "            </m0:request>\n" +
            "        </m0:getQuote>\n" +
            "    </soapenv:Body>\n" +
            "</soapenv:Envelope>");


    public static byte[] BYTE_RESPONSE = RESPONSE.toString().getBytes();

    public static byte[] getRandomUUID() {
        String x = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ver=\"http://org.apache.synapse/xsd\">\n" +
                "   <soapenv:Header/>\n" +
                "   <soapenv:Body>              \n" +
                "         <ver:return>" + java.util.UUID.randomUUID().toString() + "</ver:return>      \n" +
                "   </soapenv:Body>\n" +
                "</soapenv:Envelope>";

        return x.getBytes();
    }
}
