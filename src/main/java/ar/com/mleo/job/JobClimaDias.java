package ar.com.mleo.job;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;

import ar.com.mleo.bean.Planeta;
import ar.com.mleo.bean.Punto;
import ar.com.mleo.utils.ClimaTipos;
import ar.com.mleo.utils.FuncionCuadratica;
import ar.com.mleo.utils.MatematicaUtils;
import ar.com.mleo.utils.Triangulo;

public class JobClimaDias {
	
	private static final String FILE_JOB_DIAS_LLUVIA = "/archivos_mleo/insert-dias-lluvia.sql";
	private static final String FILE_JOB_DIAS_SEQUIA = "/archivos_mleo/insert-dias-sequia.sql";
	private static final String FILE_JOB_DIAS_IDEAL = "/archivos_mleo/insert-dias-ideal.sql";
	
	private static final long ULTIMO_DIA = 365 * 10;
	private static Planeta ferengisPlaneta = null;
	private static Planeta betasoidesPlaneta = null;
	private static Planeta vulcanosPlaneta = null;
	private static Punto sol = null;

	static {
		ferengisPlaneta = new Planeta("ferengis", "1", "1", "500");
		betasoidesPlaneta = new Planeta("betasoides", "3", "1", "2000");
		vulcanosPlaneta = new Planeta("vulcanos", "5", "1", "1000");
		sol = new Punto();
		sol.setX(new BigDecimal(0));
		sol.setY(new BigDecimal(0));
	}

	public static void main(String[] args) {
		jobPeridoLLuvia();
		jobPeridoSequia();
		jobPeridoIdeal();
		
		System.out.println("Se generaron exitosamente las condiciones de todos los días. Favor de revisar los archivos: ");
		System.out.println(FILE_JOB_DIAS_LLUVIA);
		System.out.println(FILE_JOB_DIAS_SEQUIA);
		System.out.println(FILE_JOB_DIAS_IDEAL);
	}

	private static void jobPeridoLLuvia() {
		long dia = 1;
		double perimetro =  0;
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_JOB_DIAS_LLUVIA))) {

			while(dia <= ULTIMO_DIA){
				Punto fp = MatematicaUtils.getCoordeadasRectangular(ferengisPlaneta.getRadio(), ferengisPlaneta.getAngulo(), ferengisPlaneta.getPeriodo(), new BigDecimal(dia));
				Punto bp = MatematicaUtils.getCoordeadasRectangular(betasoidesPlaneta.getRadio(), betasoidesPlaneta.getAngulo(), betasoidesPlaneta.getPeriodo(), new BigDecimal(dia));
				Punto vp = MatematicaUtils.getCoordeadasRectangularAntihorario(vulcanosPlaneta.getRadio(), vulcanosPlaneta.getAngulo(), vulcanosPlaneta.getPeriodo(),  new BigDecimal(dia));

				double area = Triangulo.getArea(fp, bp, vp);
				if(area > 0){
					if(Triangulo.esPuntoInteriorTriangulo(fp, bp, vp, sol)){
						perimetro = MatematicaUtils.getPerimetro(fp, bp, vp);
						String insert = getStringInsertDias(dia, ClimaTipos.LLUVIA_I.getValorI(), perimetro);
						bw.write(insert+"\n");
					}
				}
				dia++;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	private static void jobPeridoSequia() {
		long dia = 1;

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_JOB_DIAS_SEQUIA))) {

			while(dia <= ULTIMO_DIA){
				Punto fp = MatematicaUtils.getCoordeadasRectangular(ferengisPlaneta.getRadio(), ferengisPlaneta.getAngulo(), ferengisPlaneta.getPeriodo(), new BigDecimal(dia));
				Punto bp = MatematicaUtils.getCoordeadasRectangular(betasoidesPlaneta.getRadio(), betasoidesPlaneta.getAngulo(), betasoidesPlaneta.getPeriodo(), new BigDecimal(dia));
				Punto vp = MatematicaUtils.getCoordeadasRectangularAntihorario(vulcanosPlaneta.getRadio(), vulcanosPlaneta.getAngulo(), vulcanosPlaneta.getPeriodo(),  new BigDecimal(dia));

				double area = Triangulo.getArea(fp, bp, vp);
				if(-1<area && area<1){

					FuncionCuadratica recta = new FuncionCuadratica(fp, bp);

					BigDecimal yaux = recta.getValorY(sol.getX());
					if(MatematicaUtils.esSemejante(yaux, bp.getY(), 0.5)){
						String insert = getStringInsertDias(dia, ClimaTipos.SEQUIA_I.getValorI());
						bw.write(insert+"\n");
					}
				}
				dia++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void jobPeridoIdeal() {
		long dia = 1;

		try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_JOB_DIAS_IDEAL))) {

			while(dia <= ULTIMO_DIA){
				Punto fp = MatematicaUtils.getCoordeadasRectangular(ferengisPlaneta.getRadio(), ferengisPlaneta.getAngulo(), ferengisPlaneta.getPeriodo(), new BigDecimal(dia));
				Punto bp = MatematicaUtils.getCoordeadasRectangular(betasoidesPlaneta.getRadio(), betasoidesPlaneta.getAngulo(), betasoidesPlaneta.getPeriodo(), new BigDecimal(dia));
				Punto vp = MatematicaUtils.getCoordeadasRectangularAntihorario(vulcanosPlaneta.getRadio(), vulcanosPlaneta.getAngulo(), vulcanosPlaneta.getPeriodo(),  new BigDecimal(dia));

				double area = Triangulo.getArea(fp, bp, vp);
				if(-1<area && area<1){

					FuncionCuadratica recta = new FuncionCuadratica(fp, bp);

					BigDecimal yaux = recta.getValorY(sol.getX());
					if(!MatematicaUtils.esSemejante(yaux, bp.getY(), 0.5)){
						String insert = getStringInsertDias(dia, ClimaTipos.IDEAL_I.getValorI());
						bw.write(insert+"\n");
					}
				}
				dia++;
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getStringInsertDias(long dia, int clima) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("INSERT INTO public.dias(numero, id_climas) ").append("VALUES (").
		append(dia).append(", ").
		append(clima).append(");");
		return stringBuilder.toString();
	}
	
	private static String getStringInsertDias(long dia, int clima, double perimetro) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("INSERT INTO public.dias(numero, id_climas, intensidad_lluvia) ").append("VALUES (")
		.append(dia).append(", ").append(clima)
		.append(", ").append(perimetro)
		.append(");");
		return stringBuilder.toString();
	}

}