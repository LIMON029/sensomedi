package com.sensomedi.sensomedi.service.dto

class EmailForm(code:Int) {
    private val code:Int
    private val emailForm = "<!DOCTYPE html>\n" +
            "<html lang=\"ko\" xmlns=\"http://www.w3.org/1999/xhtml\">\n" +
            "<head>\n" +
            "    <meta http-equiv=\"Content-Type\" content=\"text/html\" charset=\"UTF-8\"/>\n" +
            "    <title>Email Template</title>\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\"/>\n" +
            "</head>\n" +
            "<body>\n" +
            "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" id=\"bodyTable\" style=\"text-align: center;\">\n" +
            "    <tr>\n" +
            "        <td>\n" +
            "            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"50%\" style=\"display: inline-table;background-color: #b3b3b3;padding: 10px;border-collapse: separate;border-radius: 8px;\">\n" +
            "                <tr>\n" +
            "                    <td>\n" +
            "                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
            "                            <tr>\n" +
            "                                <td><h1 style=\"line-height: 20px;\">인증을 진행해주세요</h1></td>\n" +
            "                            </tr>\n" +
            "                        </table>\n" +
            "                    </td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td>\n" +
            "                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
            "                            <tr>\n" +
            "                                <td><h3 style=\"line-height: 20px;margin-top: 5px;font-weight: lighter;\">어플에 인증번호를 입력하세요</h3></td>\n" +
            "                            </tr>\n" +
            "                        </table>\n" +
            "                    </td>\n" +
            "                </tr>\n" +
            "                <tr>\n" +
            "                    <td>\n" +
            "                        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"background-color: #eaeaea;border-collapse: collapse;border-radius: 8px;\">\n" +
            "                            <tr>\n" +
            "                                <td>\n" +
            "                                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
            "                                        <tr>\n" +
            "                                            <td style=\"width: 30%;\"><p style=\"text-align: center;\">인증번호</p></td>\n" +
            "                                            <td>\n" +
            "                                              <p style=\"text-align: center;margin-right: 10px;background-color: white;border-collapse: collapse;border-radius: 8px;\">${code}</p>\n" +
            "                                            </td>\n" +
            "                                        </tr>\n" +
            "                                    </table>\n" +
            "                                </td>\n" +
            "                            </tr>\n" +
            "                        </table>\n" +
            "                    </td>\n" +
            "                </tr>\n" +
            "            </table>\n" +
            "        </td>\n" +
            "    </tr>\n" +
            "</table>\n" +
            "</body>\n" +
            "</html>"
    init {
        this.code = code
    }

    fun getForm():String {
        return emailForm
    }
}