# 로그인

1. 로그인 시도 
* redis id/ip 존재 하는지 체크
	존재하면 로그인 풀 것인지 여부 물어봄

* 존재하지 않으면 id/pw DB에 존재 여부 확인
	DB에 계정 정보가 존재 시 token 생성 후 리턴 redis에 id,ip,token 저장

redis (id, ip, token, 만료시간)

만료시간은 redis 에서도 관리하고 토큰에서도 관리

토큰 access token , refresh token

2. 클라이언트가 로그인을 요청하고 성공하면 서버는 Access Token과 Refresh Token을 함께 제공한다.
* refresh 토큰은 데이터 베이스에 저장된 정보를 가져온다
* Access Token이 만료 되었다면 클라이언트는 refresh Token을 전달하여 저장된 RTK 와 비교하여 일치했을 경우 ATK를 재발급 받는다
* 사용자가 로그아웃 시 REDIS에 정보도 삭제하고 RTK도 제거한다
* 
