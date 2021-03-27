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
		String path = "/Users/tomoaki3284/IdeaProjects/courseSelector/current-semester.json";
		String key_name = Paths.get(path).getFileName().toString();
		
		String[] credentialInfo = getCredentials();
		
		assert(credentialInfo[0] != null && credentialInfo[1] != null && credentialInfo[2] != null);
		
		AWSCredentials credentials = new BasicAWSCredentials(
			credentialInfo[0],
			credentialInfo[1]
		);
		
		assert key_name != null;
		
		System.out.format("Uploading %s to S3 bucket %s...\n", key_name, bucket_name);
		final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.US_EAST_1).build();
		try {
			System.out.println("uploading...");
			PutObjectRequest por = new PutObjectRequest(bucket_name, key_name, new File(path));
			por.setCannedAcl(CannedAccessControlList.PublicRead);
			s3.putObject(por);
		} catch (AmazonServiceException e) {
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


