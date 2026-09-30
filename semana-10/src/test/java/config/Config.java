package config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Lee config/<ambiente>.properties una sola vez y expone los valores.
// El ambiente se elige al correr con -Denv=qa / -Denv=dev (por defecto: qa).
public final class Config {

    // El logger va primero: el bloque static de abajo lo usa
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    private static final String AMBIENTE = System.getProperty("env", "qa");
    private static final Properties props = new Properties();

    static {
        String archivo = "config/" + AMBIENTE + ".properties";
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream(archivo)) {
            if (in == null) {
                throw new IllegalStateException("No existe el archivo de configuración: " + archivo);
            }
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer " + archivo, e);
        }
        log.info("Ambiente de ejecución: {}", AMBIENTE);
    }

    private Config() {
        // clase utilitaria: no se instancia
    }

    // Un -Dclave=valor al correr tiene prioridad sobre el archivo
    public static String get(String clave) {
        String valor = System.getProperty(clave, props.getProperty(clave));
        if (valor == null) {
            throw new IllegalArgumentException(
                    "Falta la clave '" + clave + "' en config/" + AMBIENTE + ".properties");
        }
        return valor;
    }
}