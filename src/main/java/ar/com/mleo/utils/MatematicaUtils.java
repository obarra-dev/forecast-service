package ar.com.mleo.utils;

import ar.com.mleo.bean.Punto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class MatematicaUtils {

    private MatematicaUtils() {
    }

    /**
     * Metodo para obtener las coordenadas x e y en funcion
     * del angulo desplazado y el radio de la circunferencia.
     * Sentido horario
     *
     * @param radio
     * @param angulo
     * @param periodo
     * @param tiempo
     * @return
     */
    public static Punto getCoordeadasRectangular(final BigDecimal radio,
                                                 final BigDecimal angulo,
                                                 final BigDecimal periodo,
                                                 final BigDecimal tiempo) {
        BigDecimal velocidadAngular = angulo.divide(periodo);
        BigDecimal thita = velocidadAngular.multiply(tiempo);
        Double cos = Math.cos(thita.doubleValue());
        BigDecimal x = radio.multiply(new BigDecimal(cos.toString()));

        Double sin = Math.sin(thita.doubleValue());
        BigDecimal y = radio.multiply(new BigDecimal(sin.toString()));

        Punto punto = new Punto();
        punto.setX(x);
        punto.setY(y);

        return punto;
    }

    /**
     * Metodo para obtener las coordenadas x e y en funcion
     * del angulo desplazado y el radio de la circunferencia
     * Sentido antihorario
     *
     * @param radio
     * @param angulo
     * @param periodo
     * @param tiempo
     * @return
     */
    public static Punto getCoordeadasRectangularAntihorario(final BigDecimal radio,
                                                            final BigDecimal angulo,
                                                            final BigDecimal periodo,
                                                            final BigDecimal tiempo) {
        BigDecimal velocidadAngular = angulo.divide(periodo);
        BigDecimal thita = velocidadAngular.multiply(tiempo);
        Double cos = Math.cos(thita.doubleValue());
        BigDecimal x = radio.multiply(new BigDecimal(cos.toString()));

        Double sin = -Math.sin(thita.doubleValue());
        BigDecimal y = radio.multiply(new BigDecimal(sin.toString()));

        Punto punto = new Punto();
        punto.setX(x);
        punto.setY(y);

        return punto;
    }

    /**
     * Devuelve true si los tres puntos pasados
     * por parametro estan alineados en una recta.
     * Utiliza el algoritmo de alineacion por segmentos
     *
     * @param a
     * @param b
     * @param c
     * @return
     */
    public static boolean estanAlineados(final Punto a,
                                         final Punto b,
                                         final Punto c) {
        BigDecimal ab = (b.getX().subtract(a.getX()))
                .divide((c.getX().subtract(b.getX())), 8, RoundingMode.HALF_UP);
        BigDecimal bc = (b.getY().subtract(a.getY()))
                .divide((c.getY().subtract(b.getY())), 8, RoundingMode.HALF_UP);
        if (esSemejante(ab, bc, 0.24)) {
            return true;
        }
        return false;
    }

    /**
     * Devulve true si los dos numeros se asemejan,
     * se tiene en cuenta el @param desvio.
     *
     * @param yaux
     * @param y
     * @param desvio
     * @return
     */
    public static boolean esSemejante(final BigDecimal yaux,
                                      final BigDecimal y,
                                      final double desvio) {
        double ymin = yaux.doubleValue() - desvio;
        double ymax = yaux.doubleValue() + desvio;
        if (ymin < y.doubleValue() && y.doubleValue() < ymax) {
            return true;
        }
        return false;
    }
}