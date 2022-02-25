package com.sensomedi.sensomedi.utils

object ERROR_CODE {
    const val OK = 200

    const val CODE_NOT_EXIST = 600
    const val CODE_NOT_MATCHED = 601
    const val EMAIL_EXIST = 602
    const val USERNAME_EXIST = 603
    const val PASSWORD_NOT_MATCHED = 604
    const val PASSWORD_NOT_MATCHED_FOR_UPDATE = 6041
    const val PASSWORD_NOT_MATCHED_FOR_DELETE = 6042
    const val PASSWORD_TOO_SHORT = 605
    const val PASSWORD_TOO_SHORT_FOR_UPDATE= 6051
    const val ILLEGAL_EMAIL = 606

    const val UNAUTHORIZED_ERROR = 610
    const val LOGIN_FAILED = 611
    const val DUPLICATED_LOGIN_REQUEST_ERROR = 612
    const val UNKNOWN_USER = 613
    const val SEND_TEMP_PASSWORD_FAILED = 614

    const val DOWNLOAD_ACCESS_DENIED = 700

    const val VIEW_ACCESS_DENIED = 710
    const val NO_FILE_FOR_VIEW = 711

    const val EXIST_FILE_NAME = 720

    const val TOKEN_RECREATE_ERROR = 800
    const val TOKEN_EXPIRED_ERROR = 801
    const val UNKNOWN_TOKEN = 802
    const val TOKEN_UNKNOWN_USER = 803
}