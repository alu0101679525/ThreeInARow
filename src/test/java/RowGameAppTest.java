import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas unitarias y de integracion del punto de entrada {@link RowGameApp}.
 */
@Tag("unit")
@Tag("app")
public class RowGameAppTest {

    private List<Frame> framesBefore;

    /**
     * Registra las ventanas existentes antes de ejecutar cada prueba
     * y verifica que el entorno grafico este disponible.
     */
    @BeforeEach
    public void setUp() {
        Assumptions.assumeFalse(GraphicsEnvironment.isHeadless(),
                "Las pruebas de RowGameApp requieren entorno grafico (no headless)");
        framesBefore = Arrays.asList(Frame.getFrames());
    }

    /**
     * Cierra y libera cualquier ventana creada durante la ejecucion de la prueba.
     */
    @AfterEach
    public void tearDown() throws Exception {
        if (GraphicsEnvironment.isHeadless()) {
            return;
        }
        SwingUtilities.invokeAndWait(() -> {
            for (Frame frame : Frame.getFrames()) {
                if (!framesBefore.contains(frame)) {
                    frame.dispose();
                }
            }
        });
    }

    /**
     * Obtiene la ventana JFrame visible recien creada tras invocar {@code main}.
     */
    private JFrame awaitNewVisibleFrame() throws Exception {
        final JFrame[] found = new JFrame[1];
        SwingUtilities.invokeAndWait(() -> {
            List<JFrame> newFrames = Arrays.stream(Frame.getFrames())
                    .filter(frame -> !framesBefore.contains(frame))
                    .filter(JFrame.class::isInstance)
                    .map(JFrame.class::cast)
                    .filter(Frame::isVisible)
                    .collect(Collectors.toList());
            if (!newFrames.isEmpty()) {
                found[0] = newFrames.get(newFrames.size() - 1);
            }
        });
        return found[0];
    }

    /**
     * Comprueba que la clase se puede instanciar correctamente.
     */
    @Test
    @DisplayName("RowGameApp se instancia sin errores")
    public void testAppInstantiation() {
        RowGameApp app = new RowGameApp();
        assertNotNull(app);
    }

    /**
     * Comprueba que al ejecutar main sin argumentos se abre la ventana
     * con las reglas por defecto de Tres en raya.
     */
    @Test
    @Tag("three-in-a-row")
    @DisplayName("Ejecutar main sin argumentos inicia Tres en raya")
    public void testMainDefaultStartsThreeInARow() throws Exception {
        RowGameApp.main(new String[]{});

        JFrame frame = awaitNewVisibleFrame();
        assertNotNull(frame);
        assertTrue(frame.isVisible());
        assertEquals("Three In A Row", frame.getTitle());
    }

    /**
     * Comprueba que al pasar "ThreeInARow" como argumento se inicia
     * la variante Tres en raya.
     */
    @Test
    @Tag("three-in-a-row")
    @DisplayName("Ejecutar main con ThreeInARow inicia Tres en raya")
    public void testMainExplicitThreeInARow() throws Exception {
        RowGameApp.main(new String[]{"ThreeInARow"});

        JFrame frame = awaitNewVisibleFrame();
        assertNotNull(frame);
        assertTrue(frame.isVisible());
        assertEquals("Three In A Row", frame.getTitle());
    }

    /**
     * Comprueba que al pasar cualquier argumento distinto de "ThreeInARow"
     * se abre la ventana configurada para Tic-Tac-Toe.
     */
    @Test
    @Tag("tic-tac-toe")
    @DisplayName("Ejecutar main con otro argumento inicia Tic-Tac-Toe")
    public void testMainWithOtherArgStartsTicTacToe() throws Exception {
        RowGameApp.main(new String[]{"TicTacToe"});

        JFrame frame = awaitNewVisibleFrame();
        assertNotNull(frame);
        assertTrue(frame.isVisible());
        assertEquals("Tic Tac Toe", frame.getTitle());
    }

    /**
     * Comprueba que pasar null como arreglo de argumentos lanza NullPointerException.
     */
    @Test
    @DisplayName("Ejecutar main con args nulo lanza NullPointerException")
    public void testMainNullArgsThrowsException() {
        assertThrows(NullPointerException.class, () -> RowGameApp.main(null));
    }
}