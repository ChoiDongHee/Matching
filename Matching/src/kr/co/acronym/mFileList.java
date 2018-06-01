package kr.co.acronym;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Comparator;

import org.opencv.core.Core;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgcodecs.Imgcodecs;

public class mFileList {

	static String filename1 = "";
	static String filename2 = "D:\\ProjectCH\\find_image\\START1.jpg";
	static String path = "C:\\Users\\kth\\Documents\\Bandicam";
	
	public static void main(String[] args) {
		// Set image path
		//String filename1 = "D:\\sample\\1_org.jpg";
	//	String filename2 = "D:\\sample\\paris_02.jpg";
		String path = "D:\\ProjectCH\\EXE";
		for(int i =0 ; i < 10000 ; i++) {
			
			
			try {
				getLastFile(path);
				//Thread.sleep(100);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	  public static void fileDelete(String file_path) {
	        
	        if(null!=file_path){
	        
	            File del_File = new File(file_path);
	            
	            if(del_File.exists()){
	                 System.out.println(del_File.getName() + " 파일을 삭제합니다");
	                del_File.delete();
	            }else{
	                System.out.println(del_File.getName() + " 파일이 존재하지 않습니다.");
	            }
	        }
	    }
	public static String getLastFile(String path) {
		File fileDir = new File(path);
		
		//Arrays.sort(fileDir, new ModifiedDate());
		
		String files[] = fileDir.list();
		for(String fNm : files){
		
				int ret = 0;
			
		 System.out.println("fNm:"+fNm);//파일명
		 File file = new File(path+fNm);
		 	
		 	String locaFile =path+"\\"+ fNm ;
		 //	 System.out.println("locaFile:"+locaFile);//파일명
			String findImage = "D:\\find_image\\"+fNm;
			
		 	 ret = compareFeature(locaFile, filename2);
			
			if (ret > 0) {				

				try {
					fileCopy(locaFile,findImage);
					fileDelete(locaFile);
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Two images are same.");
				System.out.println("locaFile:"+locaFile);//파일명
			} else {
				//System.out.println("Two images are different.");
				fileDelete(locaFile);
			}
		 
		 
		 
		 //file객체를 가지고 필요한 로직 수행.
		} 
		return null;
	}
	
	
	  public void fileMove(String inFileName, String outFileName){
	        byte[] buf = new byte[1024];
	        FileInputStream fin = null;
	        FileOutputStream fout = null;
	        
	        File file = new File(inFileName);
	        if(!file.renameTo(new File(outFileName))){    // renameTo로 이동 실패할 경우
	            
	            buf = new byte[1024];
	            try {
	                fin = new FileInputStream(inFileName);
	                fout = new FileOutputStream(outFileName);
	                int read = 0;
	                while((read=fin.read(buf,0,buf.length))!=-1){
	                    fout.write(buf, 0, read);
	                }
	                 
	                fin.close();
	                fout.close();
	                file.delete();                        // 복사 후 파일 삭제
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        } 
	    }
	
public static void fileCopy(String inFileName, String outFileName) {
        
        try {
            
            FileInputStream fis = new FileInputStream(inFileName);
            FileOutputStream fos = new FileOutputStream(outFileName);
            
            int data = 0;
            
            while((data=fis.read())!=-1) {
                fos.write(data);
            }
            fis.close();
            fos.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public static String  getFile(String fullPath) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fullPath)));
			String readStr = "";
			while((readStr = reader.readLine()) != null) {
			 System.out.println("readStr:"+readStr);
			}			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return null;
	}

	public class ModifiedDate implements Comparator
	{
			public int compare(Object o1, Object o2)
			{
				File f1 = (File)o1;
				File f2 = (File)o2;
				if (f1.lastModified() > f2.lastModified())
				return -1;
			
				if (f1.lastModified() == f2.lastModified())
				return 0;
		
				return 1;
			}

	}
	
	
	/**
	 * Compare that two images is similar using feature mapping  
	 * @author minikim
	 * @param filename1 - the first image
	 * @param filename2 - the second image
	 * @return integer - count that has the similarity within images 
	 */
	public static int compareFeature(String filename1, String filename2) {
		int retVal = 0;
		long startTime = System.currentTimeMillis();
		
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		// Load images to compare
		Mat img1 = Imgcodecs.imread(filename1, Imgcodecs.CV_LOAD_IMAGE_COLOR);
		Mat img2 = Imgcodecs.imread(filename2, Imgcodecs.CV_LOAD_IMAGE_COLOR);

		// Declare key point of images
		MatOfKeyPoint keypoints1 = new MatOfKeyPoint();
		MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
		Mat descriptors1 = new Mat();
		Mat descriptors2 = new Mat();

		// Definition of ORB key point detector and descriptor extractors
		FeatureDetector detector = FeatureDetector.create(FeatureDetector.ORB); 
		DescriptorExtractor extractor = DescriptorExtractor.create(DescriptorExtractor.ORB);

		// Detect key points
		detector.detect(img1, keypoints1);
		detector.detect(img2, keypoints2);
		
		// Extract descriptors
	
		extractor.compute(img1, keypoints1, descriptors1);
		extractor.compute(img2, keypoints2, descriptors2);

		// Definition of descriptor matcher
		DescriptorMatcher matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);

		// Match points of two images
		MatOfDMatch matches = new MatOfDMatch();
//		System.out.println("Type of Image1= " + descriptors1.type() + ", Type of Image2= " + descriptors2.type());
//		System.out.println("Cols of Image1= " + descriptors1.cols() + ", Cols of Image2= " + descriptors2.cols());
		
		// Avoid to assertion failed
		// Assertion failed (type == src2.type() && src1.cols == src2.cols && (type == CV_32F || type == CV_8U)
		if (descriptors2.cols() == descriptors1.cols()) {
			matcher.match(descriptors1, descriptors2 ,matches);
	
			// Check matches of key points
			DMatch[] match = matches.toArray();
			double max_dist = 0; double min_dist = 100;
			
			for (int i = 0; i < descriptors1.rows(); i++) { 
				double dist = match[i].distance;
			    if( dist < min_dist ) min_dist = dist;
			    if( dist > max_dist ) max_dist = dist;
			}
			System.out.println("max_dist=" + max_dist + ", min_dist=" + min_dist);
			
		    // Extract good images (distances are under 10)
			for (int i = 0; i < descriptors1.rows(); i++) {
				if (match[i].distance <= 10) {
					retVal++;
				}
			}
			System.out.println("matching count=" + retVal);
		}
		
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("estimatedTime=" + estimatedTime + "ms");
		
		return retVal;
	}
	
	
	
}

