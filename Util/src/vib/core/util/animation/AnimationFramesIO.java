package vib.core.util.animation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import vib.core.util.math.Quaternion;
import vib.core.util.math.Vec3d;

/**
 *
 * @author Jing Huang
 */
public class AnimationFramesIO {

    public static void write(List<AnimationFrame> frames, String fileName) {
        FileOutputStream fop = null;
        File file;
        String content = "";
        for (AnimationFrame frame : frames) {
            //head
            int sizet = frame.getTranslations().size();
            int sizer = frame.getRotations().size();
            int fn = frame.getFrameNumber();
            content += "frame "+fn +" size "+sizer +" "+sizet+ "\n";
            //rotation
            List<Quaternion> q = frame.getRotations();
            int idx = 0;
            for (Quaternion v : q) {
                content +="r "+ idx+" "+ v.x() + " " + v.y() + " " + v.z() + " " + v.w() + "\n";
                idx++;
            }
            //translation
            idx = 0;
            List<Vec3d> tr = frame.getTranslations();
            for (Vec3d v : tr) {
                content +="t "+ idx+" "+v.x() + " " + v.y() + " " + v.z() + "\n";
                idx++;
            }
            //finish
            content += "\n";
        }
        try {
            file = new File(fileName);
            fop = new FileOutputStream(file);

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            // get the content in bytes
            byte[] contentInBytes = content.getBytes();

            fop.write(contentInBytes);
            fop.flush();
            fop.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fop != null) {
                    fop.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //TODO
    public static void read(String fileName, List<AnimationFrame> frames) {
        try {
            File file = new File(fileName);
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line;
            AnimationFrame current = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] tokens = line.split(" ");
                if(tokens.length <= 0){
                    continue;
                }else if(tokens[0].equalsIgnoreCase("frame")){
                    int frame = Integer.parseInt(tokens[1]);
                    int ts = Integer.parseInt(tokens[3]);
                    int rs = Integer.parseInt(tokens[4]);
                    current = new AnimationFrame(ts, rs);
                    frames.add(current);
                }else if(tokens[0].equalsIgnoreCase("r")){
                    int idx = Integer.parseInt(tokens[1]);
                    int x = Integer.parseInt(tokens[2]);
                    int y = Integer.parseInt(tokens[3]);
                    int z = Integer.parseInt(tokens[4]);
                    int w = Integer.parseInt(tokens[5]);
                    current.setRotation(idx, new Quaternion(x,y,z,w));
                }else if(tokens[0].equalsIgnoreCase("t")){
                    int idx = Integer.parseInt(tokens[1]);
                    int x = Integer.parseInt(tokens[2]);
                    int y = Integer.parseInt(tokens[3]);
                    int z = Integer.parseInt(tokens[4]);
                    current.setTranslation(idx, new Vec3d(x,y,z));
                }
            }
            fileReader.close();
            System.out.println("Contents of file:");
            System.out.println(stringBuffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
