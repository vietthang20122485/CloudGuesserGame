package net.codejava;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class GameController {

	@Autowired
	private PlayerService playerService;

	private static final List<String> SERVICES = List.of(
			// Analytics Services
			"Athena", "Cloudsearch", "Elasticsearch Service", "EMR", "FinSpace", "Kinesis",
			"Managed Streaming for Apache Kafka", "Redshift", "QuickSight", "Data Exchange", "Data Pipeline", "Glue",
			"Lake Formation",

			// Application Integration Services
			"Step Functions", "AppFlow", "EventBridge", "Managed Workflows for Apache Airflow", "MQ",
			"Simple Notification Service", "Simple Queue Service", "AppSync",

			// Blockchain Services
			"Managed Blockchain", "Quantum Ledger Database (QLDB)",

			// Business Applications Services
			"Alexa for Business", "Chime", "Honeycode", "WorkDocs", "WorkMail",

			// Cloud Financial Management Services
			"Cost Explorer", "Budgets", "Cost and Usage Report", "Reserved Instance Reporting", "Savings Plans",

			// Compute Services
			"EC2 Auto Scaling", "App Runner", "Batch", "Elastic Beanstalk", "Outposts",
			"Serverless Application Repository", "Snow Family", "Wavelength", "VMWare Cloud on AWS",

			// Container Services
			"Elastic Container Registry", "Elastic Container Service", "ECS Anywhere", "Elastic Kubernetes Service",
			"EKS Distro", "App2Container", "Copilot", "Fargate", "Red Hat OpenShift Service on AWS",

			// Customer Engagement Services
			"Connect", "Pinpoint", "Simple Email Service",

			// Database Services
			"Aurora", "DynamoDB", "DocumentDB", "ElastiCache", "Keyspaces", "Neptune", "Quantum Ledger Database", "RDS",
			"RDS on VMware", "Timestream", "Database Migration Service",

			// Developer Tools
			"CodeGuru", "Correto", "Cloud Development Kit", "Cloud9", "CloudShell", "CodeArtifact", "CodeBuild",
			"CodeCommit", "CodeDeploy", "CodePipeline", "CodeStar", "Command Line Interface", "Device Farm",
			"Fault Injection Simulator", "Tools and SDKs", "X-Ray",

			// End User Computing Services
			"AppStream 2.0", "WorkLink", "WorkSpaces",

			// Front-End Web and Mobile Services
			"Amplify", "Location Service", "Device Farm",

			// Game Tech Services
			"GameLift", "Lumberyard",

			// Internet of Things Services
			"IoT Core", "Greengrass", "IoT 1-Click", "IoT Analytics", "IoT Button", "IoT Device Defender",
			"IoT Management", "IoT Events", "IoT SiteWise", "IoT Things Graph", "Partner Device Catalog", "FreeRTOS",

			// Machine Learning Services
			"SageMaker", "Augmented AI", "CodeGuru", "Comprehend", "DevOps Guru", "Elastic Inference", "Forecast",
			"Fraud Detector", "Healthlake", "Kendra", "Lex", "Lookout for Equipment", "Lookout for Metrics",
			"Lookout for Vision", "Minitron", "Personalize", "Polly", "Rekognition", "SageMaker Data Wrangler",
			"SageMaker Ground Truth", "Textract", "Translate", "Transcribe", "Deep Learning AMIs",
			"Deep Learning Containers", "DeepComposer", "DeepLens", "DeepRacer", "Inferentia", "Panorama",
			"PyTorch on AWS", "Apache MXNet on AWS", "TensorFlow on AWS",

			// Management and Governance Services
			"CloudWatch", "Auto Scaling", "Chatbot", "CloudFormation", "CloudTrail", "Compute Optimizer", "Config",
			"Control Tower", "Console Mobile Application", "Distro for OpenTelemetry", "Launch Wizard",
			"License Manager", "Management Console", "Managed Services", "Managed Service for Grafana",
			"Management Service for Prometheus", "OpsWorks", "Organizations", "Personal Health Dashboard", "Proton",
			"Service Catalog", "Systems Manager", "Trusted Advisor", "Well-Architected Tool",

			// Media Services
			"Elastic Transcoder", "Interactive Video Service", "Kinesis Video Streams", "Elemental MediaConnect",
			"Elemental MediaConvert", "Elemental MediaLive", "Elemental MediaPackage", "Elemental MediaStore",
			"Elemental MediaTailor", "Elemental Appliances and Software", "Nimble Studio",

			// Migration and Transfer Services
			"Migration Hub", "Application Discovery Service", "Application Migration Service",
			"Database Migration Service", "Transfer Family", "Migration Evaluator",

			// Networking and Content Delivery Services
			"VPC", "CloudFront", "Route 53", "PrivateLink", "App Mesh", "Cloud Map", "Direct Connect",
			"Global Accelerator", "Transit Gateway", "Elastic Load Balancing",

			// Quantum Technologies Services
			"Braket",

			// Robotics Services
			"RoboMaker",

			// Satellite Services
			"Ground Station",

			// Security, Identity, and Compliance Services
			"Identity & Access Management", "Cognito", "Detective", "GuardDuty", "Inspector", "Macie", "Artifact",
			"Audit Manager", "Certificate Manager", "CloudHSM", "Directory Service", "Firewall Manager",
			"Resource Access Manager", "Secrets Manager", "Security Hub", "Shield", "Single Sign-On", "WAF",

			// Serverless Services
			"Lambda", "EventBridge", "Simple Notification System", "Simple Queue Service", "Fargate", "Step Functions",

			// Storage Services
			"Simple Storage Service", "Elastic Block Store", "Elastic File System", "FSx for Lustre",
			"FSx for Windows File Server", "S3 Glacier", "Backup", "Snow Family", "Storage Facility",
			"CloudEnsure Disaster Recovery",

			// VR and AR Services
			"Sumerian");

	@GetMapping("/")
	public String gamePage(Model model, jakarta.servlet.http.HttpSession session) {
		// Shuffle and select 10 random services
		List<String> shuffledServices = new ArrayList<>(SERVICES);
		Collections.shuffle(shuffledServices);
		List<String> selectedServices = shuffledServices.subList(0, 10);
		System.out.println(selectedServices);

		// Shuffle again to select the target services
		List<String> myList = new ArrayList<>(selectedServices);
		Collections.shuffle(myList);
		List<String> targetServices = myList.subList(0, 3);
		System.out.println(targetServices);

		// Pass data to the frontend
		model.addAttribute("services", selectedServices);
		model.addAttribute("targets", targetServices);
//	        // Save correct services in session for validation
		session.setAttribute("targetServices", targetServices);
		session.setAttribute("startTime", System.currentTimeMillis());
		session.setAttribute("attempts", 0);
		session.setAttribute("inCorrectPlace", 0);
		session.setAttribute("inWrongPlace", 0);
		session.setMaxInactiveInterval(86400);
		return "game";
	}

	@GetMapping("/welcome")
	public String welcome() {
		return "welcome";
	}

	@PostMapping("/check")
	public ResponseEntity checkGuess(@RequestParam("guess1") String guess1, @RequestParam("guess2") String guess2,
			@RequestParam("guess3") String guess3, HttpSession session, Model model) {
		// Handle null guesses
		guess1 = (guess1 == null) ? "" : guess1;
		guess2 = (guess2 == null) ? "" : guess2;
		guess3 = (guess3 == null) ? "" : guess3;
		List<String> correctServices = (List<String>) session.getAttribute("targetServices");
		List<String> playerGuesses = List.of(guess1, guess2, guess3);

		// Track attempts
		Integer attempts = (Integer) session.getAttribute("attempts");
		attempts = (attempts == null) ? 1 : attempts + 1;
		session.setAttribute("attempts", attempts);

		Map<String, String> map = new HashMap<>();

		int inCorrectPlace = 0, inWrongPlace = 0;

		for (int i = 0; i < 3; i++) {
			if (playerGuesses.get(i).equals(correctServices.get(i)))
				inCorrectPlace++;
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (i != j && playerGuesses.get(i).equals(correctServices.get(j)))
					inWrongPlace++;
			}
		}

		if (inCorrectPlace == 3) {
			long startTime = (long) session.getAttribute("startTime");
			long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
			map.put("elapsedTime", String.valueOf(elapsedTime));
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			if (authentication != null && !authentication.getName().equals("")) {
				Player player = new Player();
				player.setPlayerName(authentication.getName());
				player.setTimeToFinish(elapsedTime);
				playerService.saveScore(player);
			}
		}
		map.put("attempts", String.valueOf(attempts));
		map.put("inCorrectPlace", String.valueOf(inCorrectPlace));
		map.put("inWrongPlace", String.valueOf(inWrongPlace));
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	@GetMapping("/leaderboard")
	public String showLeaderboard(@PageableDefault(size = 10) Pageable pageable, Model model) {

		Page<Player> page = playerService.getLeaderboard(pageable);

		model.addAttribute("leaderboardEntries", page.getContent());
		model.addAttribute("currentPage", page.getNumber());
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("pageSize", page.getSize());

		return "leaderboard";
	}
	
	@GetMapping("/health")
	public ResponseEntity<Map<String, String>> healthCheck() {
		Map<String, String> status = new HashMap<>();
		status.put("status", "UP");
        return new ResponseEntity<>(status, HttpStatus.OK);
    }

}