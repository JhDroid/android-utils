# 그냥 개인적으로 사용하는 유틸 함수 모듈(Library)

### 구조
- common
    - Utils : 공통 유틸 기능
        - getDeviceUUID(context: Context): String?
            - 사용자 구분을 위한 Device의 UUID값 확인
        - getAppIcon(context: Context, packageName: String): Drawable?
            - packageName에 해당하는 앱이 설치되어 있다면 아이콘을 리턴
        - getAppNameByPackage(context: Context, packageName: String): String?
            - packageName에 해당하는 앱이 설치되어 있다면 앱 이름을 리턴
        - checkWebView(context: Context): Boolean
            - WebView의 설치 여부(사용 가능 여부) 리턴
- extension
    - ActivityExt : Activity에서 사용할 확장 함수
    - ContextExt : Context 확장 함수
- handler
    - Event : ViewModel에서 이벤트 처리를 위한 핸들러
- listener
    - OnIntervalClickListener : 중복 클릭 방지(postDelayed 사용)
