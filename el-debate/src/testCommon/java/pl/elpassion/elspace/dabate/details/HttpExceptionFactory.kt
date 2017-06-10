package pl.elpassion.elspace.dabate.details

import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

fun createHttpException(errorCode: Int): HttpException {
    val response: okhttp3.Response = okhttp3.Response.Builder()
            .code(errorCode)
            .protocol(Protocol.HTTP_1_1)
            .message("error")
            .request(Request.Builder().url("http://localhost/").build())
            .build()
    val responseBody = ResponseBody.create(MediaType.parse("text/html"), "")
    val exception = HttpException(Response.error<Unit>(responseBody, response))
    return exception
}
