# 부동산 실거래 정보 (가제)

국가에서 제공하는 부동산 실거래 데이터 기반으로 부동산 실거래 정보 제공

### 배치 종류

- lawdJob: 법정동 코드 입력 배치
```$xslt
--spring.batch.job.names=lawdJob -filePath=lawd_code_230106.txt
```
- 실거래가 조회 배치

### 코드 사이트

- 법정동 코드: https://www.code.go.kr/stdcode/regCodeL.do
- 실거래가: https://www.data.go.kr/
