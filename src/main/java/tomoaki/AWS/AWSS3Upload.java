package tomoaki.AWS;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import tomoaki.courseClasses.Course;

public class AWSS3Upload {
	
	public AWSS3Upload() {
	
	}
	
	public Bucket getBucket(final String bucketName) {
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();
		Bucket named_bucket = null;
		List<Bucket> buckets = s3.listBuckets();
		for (Bucket b : buckets) {
			if (b.getName().equals(bucketName)) {
				named_bucket = b;
			}
		}
		return named_bucket;
	}
	
	public static void main(String[] args) {
		String bucket_name = "coursehelper";
		String path = "current-semester.json";
		String key_name = Paths.get(path).getFileName().toString();
		
		String[] credentialInfo = getCredentials();
		
		assert(credentialInfo[0] != null && credentialInfo[1] != null && credentialInfo[2] != null);
		
		AWSCredentials credentials = new BasicAWSCredentials(
			credentialInfo[0],
			credentialInfo[1]
		);
		
		assert key_name != null;
		
		System.out.format("Uploading %s to S3 bucket %s...\n", key_name, bucket_name);
		final AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
		try {
			System.out.println("uploading...");
			PutObjectRequest por = new PutObjectRequest(bucket_name, key_name, new File(path));
			por.setCannedAcl(CannedAccessControlList.PublicRead);
			s3.putObject(por);
		} catch (AmazonServiceException e) {
			System.out.println("Ooof Exception Here Boof");
			System.err.println(e.getErrorMessage());
			System.exit(1);
		}
		System.out.println("Done!");
	}
	
	public static String[] getCredentials() {
		String[] res = new String[3];
		
		String path = "/Users/tomoaki3284/.aws/credentials";
		File file = new File(path);
		try {
			Scanner scan = new Scanner(file);
			scan.nextLine(); // [default]
			scan.nextLine(); // region
			res[0] = scan.nextLine().split("=")[1]; // aws_access_key_id
			res[1] = scan.nextLine().split("=")[1]; // aws_secret_access_key
//			res[2] = scan.nextLine().split("=")[1]; // aws_session_token
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
}

class PreferredCustomer extends Customer {
	
	// attributes...
	private int purchase;
	
	PreferredCustomer(String name, int pur) {
		// call parent (in this case, Customer) constructor
		// pass in whatever parameter you want to init/set it to attributes
		super(name);
		// now the PreferredCustomer object has "name" attribute set through parent constructor
		
		// only thing left is to set the value for specific attribute that belong to PreferredCustomer
		// which is not in Customer class
		this.purchase = pur;
	}
	
	// getter and setter
	// accessType returnType methodName (param) {}
	public int getPurchase() {
		return purchase;
	}
	
	public void setPurchase(int purchase) {
		assert purchase >= 0; // check something you want to check before setting it
		this.purchase = purchase;
	}
}

class Customer {
	
	// attributes...
	private String name;
	
	Customer(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		assert name != null;
		this.name = name;
	}
}

class Main {
	public static void main(String[] args) {
		// Because PreferredCustomer IS Customer (PreferredCustomer extends Customer)
		// you can also store PreferredCustomer in Customer variable
		// e.g. Customer = new PreferredCustomer("name", 100);
		PreferredCustomer pCustomer = new PreferredCustomer("tomo", 1000);
		
		// call getter and get purchase value
		pCustomer.getPurchase();
		
		
		// call getter and get name value
		pCustomer.getName();
		// as you may notice, getName() method is not define in PreferredCustomer class
		// but you can call this method, because PreferredCustomer IS Customer.
		// yes, if class_A inherit class_B, class_A object can call class_B method
		
		// what if you want to change method that is in parent class?
		// you can take a look at the concept call "override"
	}
}
