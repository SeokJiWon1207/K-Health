# K-Health

### 다이어트, 운동 기록하면서 하자! 운동&식단 관리 안드로이드 애플리케이션

● #### ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 구글 플레이 다운로드 링크 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

https://play.google.com/store/apps/details?id=com.Jiwon.K_Health

-------------------------------------------------------------------------------------------------------------

## Intro
<p align="center">
<img src = "https://user-images.githubusercontent.com/76944959/168574584-f404eb28-b003-44b6-9cf9-a6cdba825dac.png" width="70%" height="50%">
</p>
<p align="center">
<img src="https://img.shields.io/badge/Android Studio-596164?style=flat-square&logo=Android&logoColor=green"/>
<img src="https://img.shields.io/badge/Kotlin-596164?style=flat-square&logo=Kotlin&logoColor=d57eeb"/>
<img src="https://img.shields.io/badge/Firebase-596164?style=flat-square&logo=firebase&logoColor=yellow"/>
<img src="https://img.shields.io/badge/Facebook-596164?style=flat-square&logo=Facebook&logoColor=white"/>
<img src="https://img.shields.io/badge/Google-596164?style=flat-square&logo=Google&logoColor=red"/>
</p>

#### 기록하고 관리하면 다이어트&운동효과 상승!
#### 먹고, 운동한 것을 캘린더 형식으로 기록하고 다른 사람들과 운동루틴 또는 식단을 공유해보세요


## 🚀 Demo

|👯 로그인 화면(소셜로그인O)|⭐ 홈 화면|💪 운동기록 화면|
|------|---|---|
|<img src = "https://user-images.githubusercontent.com/76944959/168602794-56909857-33ee-4245-a436-7ab31879347c.gif">|<img src = "https://user-images.githubusercontent.com/76944959/168603515-b218a072-f7f2-4721-9e95-c48ef685a474.gif">|<img src = "https://user-images.githubusercontent.com/76944959/168603967-9853f877-ad12-4501-bd7a-e33eaa87f2a4.gif">|

|🤷 일일 권장량 설정|🍔 식단 검색&등록 🔎|❓ 음식 상세정보|
|------|---|---|
|<img src = "https://user-images.githubusercontent.com/76944959/168607290-b11475d0-1fce-46ac-9b7a-1897cb31e0f2.gif">|<img src = "https://user-images.githubusercontent.com/76944959/168607803-94ce3c00-caa7-4b63-8252-ca3751a57b36.gif">|<img src = "https://user-images.githubusercontent.com/76944959/168609499-30c9dcfb-489f-445a-805e-4f1c7244860e.gif">|

|👬 투게더(SNS)|🎉 오늘의 임무 완료|
|------|---|
|<img src = "https://user-images.githubusercontent.com/76944959/168611812-c0c9d291-752d-4638-b1d6-1c447e34c7fe.gif">|<img src = "https://user-images.githubusercontent.com/76944959/168755229-930fc80c-cc5f-477b-a180-88d20bdb4cc3.gif">|

## ⚡️ Skills

### 사용한 라이브러리
* #### Jetpack
  * ##### Activity
    * Activity에 기반하여 빌드된 구성 가능한 API에 액세스합니다. 
  * ##### Fragment
    * Activity 내에서 호스팅되는 여러 개의 독립적인 화면으로 앱을 분할합니다.
  * ##### ViewBinding
    * findViewById()를 이용한 View참조 방법이 가진 문제[코드의 번거로움과 메소드의 동작속도 문제]를 해결하기 위해 사용했습니다.
  * ##### Recyclerview  
    * 메모리 사용량을 최소화하면서 UI에 많은 양의 데이터를 표시합니다. 운동 목록, 음식 목록 등 다수의 데이터들을 나타내기위해 사용했습니다.
  * ##### Permission 
    * 프로필 사진을 등록하기 위해 갤러리 permission 을 통해 권한 허락을 받습니다.
  * ##### Constraintlayout  
    * 쉽게 View를 구성할 수 있고, 쉽고 간편한 유지보수를 위해 사용했습니다.
  * ##### Swiperefreshlayout
    * 스와이프하여 새로고침 UI 패턴을 구현합니다. 투게더(SNS) 화면에서 새로운 게시글을 불러오기 위해 사용했습니다.
  * ##### Room  
    * SQLite 데이터베이스에서 지원하는 영구 데이터를 생성, 저장 및 관리합니다. 간단한 검색어 저장을 위해 사용했습니다.
    
* #### UI
  * ##### Linearlayout
    * VIew들을 계층적으로 배치하기 위해 사용햇습니다.
  * ##### Relativelayout
    * View들을 상대적으로 배치하기 위해 사용했습니다.
  * ##### Framelayout
    * View들을 겹쳐보이게 배치하기 위해 사용했습니다.
      
* #### Third Party
  * ##### UI 관련 프로그레스바, 버튼, 빵빠레 관련 라이브러리들을 사용했습니다.
    * 'nl.dionsegijn:konfetti-xml:2.0.2' // 빵빠레 라이브러리
    * 'com.royrodriguez:transitionbutton:0.2.0' // 로그인 버튼 라이브러리
    * "com.github.skydoves:progressview:1.1.3" // ProgressView 라이브러리
  * ##### Glide
    * 이미지를 빠르고 효율적으로 불러올 수 있게 사용했습니다. 그 외에도 이미지 로딩을 시작하기 전에 보여줄 이미지를 설정한다던지 리소스를 불러오다가 에러가 발생했을 때 보     여줄 이미지를 설정, 간단한 애니메이션 효과를 주기 위해서도 이 라이브러리를 사용했습니다.
  * ##### Retrofit
    * 식품의약품안전처에서 제공하는 [식품 영양성분 정보 Open API]를 사용하기 위해 JSON 형태를 안드로이드의 [Kotlin data class File From Json]를 이용해 data class를 생성
      하고, 단순히 정보들을 얻어오기 위해 Interface에서는 HTTP Method의 GET만을 정의해서 사용했습니다. 
  * ##### Okhttp3
    * 이 라이브러리는 2가지 목적으로 사용되었습니다. 첫 번째는 logging-interceptor를 통해 retrofit의 통신 상태를 로그캣에 [OkHttp] 단어를 통해 확인하는 목적이였고,
      두 번째는 retrofit의 3가지 타임아웃(Connection, Read, Write) 시간 설정 값을 더욱 길게 가져가기 위해 사용했습니다.
  * ##### Coroutine
    * Room, Retrofit, Firebase의 비동기 프로그래밍을 효과적으로 사용할 수 있는 coroutine을 사용했습니다. Room과 Retrofit은 자체적으로 비동기 처리를 하기 때문에 Main             Dispatcher에서 suspend function을 사용하더라도 Main-Safety 했지만, firebase의 작업 경우 UI를 데이터를 먼저 받고 작업해야하는 동기식 프로그래밍이 필요한 경우가
      있어서 IO Dispatcher에서 작업을 통해 runBlocking 시킨 후 UI를 업데이트 시키는 방식을 사용하기 위해 coroutine을 사용하기도 했습니다.
  * ##### Facebook, Google
    * 소셜 로그인을 구현하기 위해 사용했습니다.
    
## 💾 DB

### Firebase
<img src = "https://user-images.githubusercontent.com/76944959/168818100-5aef9962-ee4a-47ff-9319-71db338708bc.JPG" width="30%" height="30%">

* #### FireStore Database
  * 유지보수 없이도 수요에 맞춰 손쉽게 확장할 수 있는 서버리스 문서 데이터베이스인 FireStore를 택했고 NoSql 형식으로 데이터베이스를 사용했습니다. 더하여 실시간 동기화의 
    특징으로 인해 데이터를 간단하게 업데이트 할 수 있었습니다. 
* #### Storage
  * 이미지, 오디오, 동영상 등의 저장소로 활용할 수 있는 Firebase Storage를 이용해 사용자 설정 프로필 이미지를 데이터베이스에 넣어 관리합니다.  

### SharedPreferences
* #### 간단한 flag값이나 문자열, 정수들을 관리하기 위해 SharedPreferences를 사용했습니다.

### Room  
* #### 위에서 설명했듯이, 검색한 음식 리스트들을 저장하기 위해 사용했습니다.
  
## 🐞 버그

 ### 현재 확인된 문제점(22.05.18)
   * #### 구글 로그인 이슈가 있습니다.
   * #### 식단 삭제시 '오늘의 섭취량'이 늘어나는 문제가 있습니다. / 다른 화면으로 갔다올 시에는 정상 표기됩니다.  
   
## 📶 버전 및 업데이트 예정사항

 ### version 1.0 (현재)
   * #### 첫 런칭 버전
 ### version 1.1 (다음 업데이트 예정사항)
   * 소셜 로그인(카카오, 네이버)구현 -> 이미 구현은 해놨지만 Firebase Authencation에 연동하는 것이 문제
   * 자동로그인 기능 -> 기존에 있던 SharedPreferences를 사용하면 될 것 

## 🙋 프로젝트 참여자
|개발자|디자인|기획자|
|------|------|------|
|석지원|석지원|석지원|

## 💣 트러블 슈팅
 ##### #1 Retrofit TimeoutException -> 기본 Timeout 시간을 초과하면 연결이 안된다는것을 인지 -> OkHttpClient를 이용해 Timeout 시간 재설정
 ##### #2 홈 화면의 오늘 할 일 데이터 불일치 -> firebase는 데이터를 비동기식으로 가져온다는 사실을 인지 -> Coroutine을 사용해서 IO 쓰레드에서 데이터를 받아 온 뒤 Main 쓰레드에서 UI 업데이트 
 ##### #3 RecyclerView Adapter Edittext값들 가져오지 못함 -> TextView와는 달리 EditText는 좀 방식이 달라서 TextWatcher를 이용해 data class로 담아서 fragment에서 사용
 ##### 그 외의 문제 해결 기록 https://syt1114.tistory.com/category/Android/Error
 
 ## 🤔 후기
  ##### #1 혼자진행한점 -> 디자인, 기획, 개발을 혼자하기가 힘듦 온라인 스터디를 통해 개발 인원이라도 1명 늘렸었다면 제작 기간이 짧아지고 나와는 다른 견해를 구할 수 있지 않았을까라는 생각이 듦. 또한, 둘 이상에서 진행했다면 git의 다양한 기능을 사용해봤을텐데 하는 아쉬움.
  ##### #2 코드의 아쉬움 -> 쓰는 문법만 쓰다보니까 남의 잘 짜여진 코드들을 보니 아쉬움. 
  ##### #3 MVVM 패턴으로 개발하지 않은 것 -> Model View ViewModel의 아키텍쳐 패턴으로 MVC처럼 양방향이 아닌 단방향 패턴으로 유지보수가 능이함. 간단한 프로젝트라 사         용하진 않았지만 요즘 많이 MVVM 패턴으로 개발을 하는걸 보고 후회중 이 부분은 따로 프로젝트를 만들어서 하거나 리팩토링으로 해결하도록...
  ##### #4 앱을 출시했을때의 보람 -> 혼자 진행한만큼 기쁨이 배가 되는듯함. 다른 패턴, 기술들을 이용해 다른 앱을 만들어내고 싶다는 생각이 들음. 뭔가 안드로이드 개발         자로써 첫걸음을 내딘듯함.
  
  
## ⚠ 저작권 및 사용권 정보

### 아이콘 저작권 표시
<a href="https://www.flaticon.com/kr/free-icons/" title="아침 아이콘">아침 아이콘  제작자: Icon Place - Flaticon</a>

<a href="https://www.flaticon.com/kr/free-icons/" title="태양 아이콘">태양 아이콘  제작자: Freepik - Flaticon</a>

<a href="https://www.flaticon.com/kr/free-icons/" title="달 아이콘">달 아이콘  제작자: Freepik - Flaticon</a>

<a href="https://www.flaticon.com/kr/free-icons/" title="간식 아이콘">간식 아이콘  제작자: Freepik - Flaticon</a>

### skydove's Custom ProgressBar 
```javascript
Copyright 2019 skydoves (Jaewoong Eum)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

### Konfetti's Party Effect
```javascript
ISC License

Copyright (c) 2017 Dion Segijn

Permission to use, copy, modify, and/or distribute this software for any
purpose with or without fee is hereby granted, provided that the above
copyright notice and this permission notice appear in all copies.

THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
```       
