# 부동산 실거래 정보 (가제)

국가에서 제공하는 부동산 데이터 기반으로 부동산 실거래 정보 제공

### 환경구성

- JAVA 11
- Spring Boot 2.7.7 (with Spring Batch)
- MY-SQL

### 배치 종류

- lawdJob: 법정동 코드 입력 배치

```
--spring.batch.job.names=lawdJob -filePath=lawd_code_230106.txt
```

- aptTrInsertJob: 실거래가 데이터 입력 배치

```
--spring.batch.job.names=aptTrInsertJob -filePath=apt_api_response_sample.xml
```

http://openapi.molit.go.kr/OpenAPI_ToolInstallPackage/service/rest/RTMSOBJSvc/getRTMSDataSvcAptTradeDev?LAWD_CD=41590&DEAL_YMD=202301&serviceKey=

### 코드 사이트

- 법정동 코드: https://www.code.go.kr/stdcode/regCodeL.do
- 실거래가: https://www.data.go.kr/
