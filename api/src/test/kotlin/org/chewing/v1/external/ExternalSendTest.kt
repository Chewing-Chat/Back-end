package org.chewing.v1.external

import org.chewing.v1.config.IntegrationTest
import org.springframework.test.context.ActiveProfiles

//
@ActiveProfiles("local")
class ExternalSendTest : IntegrationTest() {

//    @Autowired
//    private lateinit var fcmClient: FcmClient

    //
//    @Test
//    fun test() {
//        val notification = TestDataFactory.createNotification()
//        val message = FcmMessageDto.from(notification)
//        fcmClient.sendMessage(message)
//    }
//    @Autowired
//    private lateinit var fileHandler: FileHandler
//
//    @Autowired
//    private lateinit var aiService: AiService
//
//    @Autowired
//    private lateinit var aiSender: AiSender
//
//    @Test
//    fun test() {
//        val imageWidth = 100
//        val imageHeight = 100
//        val bufferedImage = BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB)
//
//        // 이미지에 그래픽 그리기
//        val graphics = bufferedImage.createGraphics()
//        graphics.color = Color.BLUE
//        graphics.fillRect(0, 0, imageWidth, imageHeight)
//        graphics.color = Color.WHITE
//        graphics.drawString("Hello, World!", 10, 50)
//        graphics.dispose()
//
//        // 이미지를 바이트 배열로 변환
//        val baos = ByteArrayOutputStream()
//        ImageIO.write(bufferedImage, "jpg", baos)
//        val imageBytes = baos.toByteArray()
//
//        // MockMultipartFile 생성
//        val file = MockMultipartFile(
//            "file",
//            "0.jpg",
//            MediaType.IMAGE_JPEG_VALUE,
//            imageBytes,
//        )
//        val fileData = FileUtil.convertMultipartFileToFileData(file)
//        fileHandler.handleNewFile("testUserId", fileData, FileCategory.PROFILE)
//        val localFilePath = "C:/temp/${file.originalFilename}" // 원하는 경로로 변경하세요
//
//        // 로컬 파일로 저장하는 함수 호출
//        saveMultipartFileToLocal(file, localFilePath)
//    }
//
//    @Test
//    fun test2() {
//        val feedInfo = FeedInfo.of(
//            feedId = "testFeedId",
//            userId = "testUserId",
//            topic = "오늘 공부함",
//            likes = 0,
//            comments = 0,
//            uploadAt = LocalDateTime.now(),
//        )
//        val media = Media.of(
//            FileCategory.FEED,
//            "https://kr.object.ncloudstorage.com/chewing-bucket/PROFILE/testUserId/9ee1c9f6-793e-40bf-947b-a4380a043735/0.jpg",
//            0,
//            org.chewing.v1.model.media.MediaType.IMAGE_JPG,
//        )
//        val feedDetail = FeedDetail.of(
//            feedDetailId = "testFeedDetailId",
//            feedId = "testFeedId",
//            media = media,
//        )
//        val friendName = UserName.of("김", "츄즈")
//        val feed = Feed.of(feedInfo, listOf(feedDetail))
//        val result = aiService.getAiRecentSummary(friendName, listOf(feed))
//        println(result)

    //        val prompt = "친구님의 최근 생활을 보아하니 요즘 두바이초콜릿을 먹어보고 싶어합니다. 누군가가 선물을 해주면 김츄즈님이 행복해할 것입니다."
    //        println(aiSender.sendAiPrompt(prompt))
//}
//
//    private fun saveMultipartFileToLocal(multipartFile: MockMultipartFile, filePath: String) {
//        try {
//            val file = File(filePath)
//            // 부모 디렉토리가 없으면 생성
//            file.parentFile.mkdirs()
//            // 파일 출력 스트림을 사용하여 파일 저장
//            FileOutputStream(file).use { fos ->
//                fos.write(multipartFile.bytes)
//            }
//            println("파일이 성공적으로 저장되었습니다: $filePath")
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//    }
}
