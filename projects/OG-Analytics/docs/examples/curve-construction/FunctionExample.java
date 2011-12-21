// @export "imports"
import com.opengamma.math.function.RealPolynomialFunction1D;
import com.opengamma.math.rootfinding.BrentSingleRootFinder;
import com.opengamma.math.rootfinding.CubicRealRootFinder;
import java.io.PrintStream;
import java.util.Arrays;

// @export "classDefinition"
public class FunctionExample {
    // @export "polyDerivativeDemo"
    public static RealPolynomialFunction1D getFunction() {
        double[] coefficients = {-125,75,-15,1};
        return new RealPolynomialFunction1D(coefficients);
    }

    public static void polyDerivativeDemo(PrintStream out) {
        RealPolynomialFunction1D f = getFunction();

        assert f.evaluate(5.0) == 0.0;

        RealPolynomialFunction1D d = f.derivative();
        double[] coefficients = d.getCoefficients();
        out.println(Arrays.toString(coefficients));
    }

    // @export "cubicRealRootFindingDemo"
    public static void cubicRealRootFindingDemo(PrintStream out) {
        RealPolynomialFunction1D f = getFunction();
        CubicRealRootFinder cubic = new CubicRealRootFinder();
        java.lang.Double[] roots = cubic.getRoots(f);
        out.println(Arrays.toString(roots));
    }

    // @export "brentSingleRootFinderDemo"
    public static void brentSingleRootFinderDemo(PrintStream out) {
        RealPolynomialFunction1D f = getFunction();
        BrentSingleRootFinder brent = new BrentSingleRootFinder();
        java.lang.Double root = brent.getRoot(f,-10.0,10.0);
        out.println(root);
    }

    // @export "brentSingleRootFinderNotBracketingDemo"
    public static void brentSingleRootFinderNotBracketingDemo(PrintStream out ) {
        RealPolynomialFunction1D f = getFunction();
        BrentSingleRootFinder brent = new BrentSingleRootFinder();
        try {
            out.println("Trying to call getRoot with arguments that don't bracket the root...");
            brent.getRoot(f, -1.0, 1.0);
        } catch (java.lang.IllegalArgumentException e) {
            out.println("IllegalArgumentException called");
        }
    }

    // @end
    public static void main(String[] args) throws Exception {
        String[] ogargs = {"FunctionExample", "../dexy--function-output.json"};
        OpenGammaExampleClass.main(ogargs);
    }
}
