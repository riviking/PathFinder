intern-service/
├── .mvn/
│   └── wrapper/
│       └── maven-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── pathfinder/
│   │   │           └── intern/
│   │   │               ├── InternServiceApplication.java
│   │   │               │
│   │   │               ├── client/
│   │   │               │   └── UserServiceClient.java            # RestClient to notify user-service on notice publish
│   │   │               │
│   │   │               ├── common/
│   │   │               │   ├── ApiException.java                 # Custom runtime exception with field errors
│   │   │               │   └── GlobalExceptionHandler.java       # Handles 400, 404, 409, 413, 415 error JSON responses
│   │   │               │
│   │   │               ├── controller/
│   │   │               │   ├── CategoryController.java           # GET /api/categories (Public)
│   │   │               │   ├── InternalNoticeController.java     # GET /internal/notices/{id}/summary
│   │   │               │   ├── PublicFlyerController.java        # GET /api/flyers/{noticeId}/image, GET /api/flyers/{noticeId}
│   │   │               │   ├── StaffFlyerController.java         # POST, PUT, DELETE /api/staff/flyers/{noticeId}
│   │   │               │   ├── StaffNoticeController.java        # CRUD, publish, close for notices (Staff/Admin)
│   │   │               │   └── StudentNoticeController.java      # GET /api/notices, GET /api/notices/{id}, filter-options
│   │   │               │
│   │   │               ├── dto/
│   │   │               │   ├── CategoryDto.java
│   │   │               │   ├── FlyerResponse.java
│   │   │               │   ├── NoticeFilter.java
│   │   │               │   ├── NoticeRequest.java
│   │   │               │   ├── NoticeResponse.java
│   │   │               │   └── NoticeSummary.java
│   │   │               │
│   │   │               ├── model/
│   │   │               │   ├── Compensation.java                 # Enum: PAID, UNPAID, NOT_STATED
│   │   │               │   ├── Flyer.java                        # Flyer metadata entity (@Document("flyers"))
│   │   │               │   ├── JobCategory.java                  # Job category entity (@Document("job_categories"))
│   │   │               │   ├── Notice.java                       # Notice entity (@Document("notices"))
│   │   │               │   ├── NoticeStatus.java                 # Enum: DRAFT, PUBLISHED, CLOSED
│   │   │               │   ├── NoticeView.java                   # Student view tracking (@Document("notice_views"))
│   │   │               │   └── WorkMode.java                     # Enum: ONSITE, REMOTE, HYBRID
│   │   │               │
│   │   │               ├── repository/
│   │   │               │   ├── FlyerRepository.java              # MongoRepository for Flyer
│   │   │               │   ├── JobCategoryRepository.java        # MongoRepository for JobCategory
│   │   │               │   ├── NoticeRepository.java             # MongoRepository for Notice
│   │   │               │   └── NoticeViewRepository.java         # MongoRepository for NoticeView
│   │   │               │
│   │   │               ├── security/
│   │   │               │   ├── InternalKeyFilter.java            # Validates X-Internal-Key header for /internal/**
│   │   │               │   ├── JwtAuthFilter.java                # Parses Bearer JWT tokens into SecurityContext
│   │   │               │   ├── JwtService.java                   # Validates and parses JWT claims
│   │   │               │   └── SecurityConfig.java               # WebSecurityConfig & role-based endpoint authorization
│   │   │               │
│   │   │               └── service/
│   │   │                   ├── FlyerService.java                 # GridFS file storage, magic byte & size checks
│   │   │                   ├── NoticeRules.java                  # Validation rules for publishing notices
│   │   │                   ├── NoticeSearchService.java          # MongoTemplate criteria-based search & filtering
│   │   │                   └── StaffNoticeService.java           # Draft CRUD, publish, close, view recording
│   │   │
│   │   └── resources/
│   │       ├── application.yml                                   # Database URI (intern_db), Port 8082, JWT secret, limits
│   │       ├── static/
│   │       └── templates/
│   │
│   └── test/
│       └── java/
│           └── com/
│               └── pathfinder/
│                   └── intern/
│                       └── InternServiceApplicationTests.java
│
├── target/
├── .gitattributes
├── .gitignore
├── HELP.md
├── mvnw
├── mvnw.cmd
└── pom.xml