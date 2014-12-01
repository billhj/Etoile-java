
import org.etoile.core.math.ColumnVector3;
import org.etoile.core.math.Matrix33;
import org.etoile.core.math.Quaternion;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Jing Huang  * <gabriel.jing.huang@gmail.com or jing.huang@telecom-paristech.fr>
 */
public class Test {
    /**
     * @param args
     */
    public static void main(final String[] args) {
        Quaternion q = new Quaternion();
        q.setAxisAngle(new ColumnVector3(1,0,0), 3.14);
        ColumnVector3 v0 = new ColumnVector3(0,2,0);
        ColumnVector3 v1 = q.multiply(v0);
        Matrix33 m = q.getRotationMatrix();
        ColumnVector3 v2 = m.multiply(v0);
        System.out.println(v1);
        System.out.println(v2);
    }
}
