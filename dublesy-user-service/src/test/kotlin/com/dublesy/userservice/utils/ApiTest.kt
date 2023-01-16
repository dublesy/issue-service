package com.dublesy.userservice.utils

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.reactive.function.client.ExchangeStrategies
import org.springframework.web.reactive.function.client.WebClient

import org.springframework.web.util.DefaultUriBuilderFactory


@SpringBootTest
class ApiTest {

    @Test
    fun apiTest() {

        // xmlmapper를 사용하기 위해 build.gradle에  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.11.3") 추가

        val API_URL = "http://apis.data.go.kr/B550928/searchLtcInsttService01/getLtcInsttSeachList01"
        val SERVICEKEY = "AEKp8oyua8wi%2B59F8sqhbjMWBJ62GHaSdGlh3zTX6xaDCYEgRb4%2B5KEaeDPKlzlC6XoLg8s%2B6eCi29fshAzTww%3D%3D"

        val factory = DefaultUriBuilderFactory(API_URL)
        factory.encodingMode = DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY // 요거 해줘야 받아짐
        val wc = WebClient
                .builder()
                .exchangeStrategies( ExchangeStrategies.builder()
                        .codecs{
                            configurer -> configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024) // 파일 사이즈 제한이 있어서 10M로 늘림
                        }.build()
                ).uriBuilderFactory(factory).baseUrl(API_URL).build()

            val response: ResponseEntity<String> =
                    wc.get()
                            .uri { uri ->
                                uri.queryParam("ServiceKey", SERVICEKEY)
                                        .queryParam("siDoCd", 26)
                                        .queryParam("numOfRows", 10000)
                                        .build()
                            }
                            .accept(MediaType.APPLICATION_XML)
                            .retrieve()
                            .toEntity(String::class.java)
                            .block() as ResponseEntity<String>

            val body = response.body
            val xmlMapper: ObjectMapper = XmlMapper()
            try {
                val response = xmlMapper.readValue(body, Response::class.java)
                println(response)
            } catch(e : JsonMappingException) {
                e.stackTraceToString()
            } catch (ex: JsonProcessingException) {
                ex.stackTraceToString()
            }

    }


}



class Response{
    var header: Header? = null
    var body: Body? = null
    override fun toString(): String {
        return "Response(header=$header, body=$body)"
    }


}

class Header
{
    var resultCode: String? = null
    var resultMsg: String? = null
    override fun toString(): String {
        return "Header(resultCode=$resultCode, resultMsg=$resultMsg)"
    }


}

class Body{

    var items:List<Item> = listOf()
    var numOfRows: Int? = null
    var pageNo: Int? = null
    var totalCount: Int? = null
    override fun toString(): String {
        return "Body(items=$items, numOfRows=$numOfRows, pageNo=$pageNo, totalCount=$totalCount)"
    }


}

class Item {
    var adminNm: String? = null
    var adminPttnCd: String? = null
    var longTermAdminSym: String? = null
    var longTermPeribRgtDt: String? = null
    var siDoCd: String? = null
    var siGunGuCd: String? = null
    var stpRptDt: String? = null
    override fun toString(): String {
        return "Item(adminNm=$adminNm, adminPttnCd=$adminPttnCd, longTermAdminSym=$longTermAdminSym, longTermPeribRgtDt=$longTermPeribRgtDt, siDoCd=$siDoCd, siGunGuCd=$siGunGuCd, stpRptDt=$stpRptDt)"
    }


}