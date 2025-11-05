TextMinerDBL – API de Minería de Texto con NLP, LDA y Resumen Automático

Autor: Demien Becerra Lozano
Versión: v6.0
Tecnologías: Java 21, Spring Boot, Apache OpenNLP, MALLET, HTML, JavaScript

Descripción General

TextMinerDBL es una API desarrollada en Java que realiza análisis semántico y estructural de texto.
Combina técnicas de procesamiento de lenguaje natural (NLP), análisis de sentimiento, detección de entidades, modelado de temas mediante LDA (Latent Dirichlet Allocation) y generación de resúmenes automáticos.

El sistema además guarda los resultados de cada análisis en formato JSON y permite consultar un historial completo desde un dashboard visual.

Características Principales
Función	Descripción	Endpoint
Análisis de Sentimiento	Detecta palabras positivas y negativas, y determina el tipo de sentimiento del texto.	/api/analizar
Detección de Entidades	Reconoce personas, lugares, organizaciones y categorías varias.	/api/nlp
Resumen Automático	Genera un resumen extractivo con base en la frecuencia de palabras.	/api/resumen
Detección de Temas (LDA)	Utiliza el algoritmo MALLET para descubrir temas principales del texto.	/api/temas
Resumen Temático	Combina LDA y NLP para generar una frase representativa por tema.	/api/resumen-tematico
Análisis Completo	Integra todos los módulos de análisis en una sola respuesta.	/api/analisis-completo
Historial de Resultados	Lista todos los análisis guardados en la carpeta de resultados.
Arquitectura General
               ┌───────────────────────────────┐
               │        Cliente / Frontend     │
               │   Dashboard HTML + JavaScript │
               │   http://localhost:8080/      │
               └──────────────┬────────────────┘
                              │
                              ▼
               ┌───────────────────────────────┐
               │        API Spring Boot        │
               │    (TextMinerDBL Application) │
               ├───────────────────────────────┤
               │ AnalisisService               │ → Análisis de sentimiento
               │ NlpService                    │ → Entidades con OpenNLP
               │ ResumenService                │ → Resumen extractivo
               │ LdaService (MALLET)           │ → Temas por LDA
               │ ResumenTematicoService        │ → Resumen por tema
               │ AnalisisCompletoService       │ → Integración completa
               │ HistorialService              │ → Consulta de resultados previos
               └───────────────────────────────┘
                              │
                              ▼
               ┌───────────────────────────────┐
               │        Persistencia JSON       │
               │          /resultados/          │
               │  Guarda cada análisis con fecha│
               └───────────────────────────────┘

Estructura del Proyecto
textminer/
 ├── src/
 │   ├── main/
 │   │   ├── java/com/demien/textminer/
 │   │   │   ├── controller/
 │   │   │   ├── service/
 │   │   │   └── TextminerDblApplication.java
 │   │   ├── resources/
 │   │   │   ├── models/
 │   │   │   │   ├── en-token.bin
 │   │   │   │   └── en-sent.bin
 │   │   │   ├── stoplists/
 │   │   │   │   └── en.txt
 │   │   │   └── static/
 │   │   │       ├── index.html
 │   │   │       └── resultados/
 │   └── test/
 ├── resultados/
 ├── pom.xml
 ├── README.md
 ├── arquitectura.png
 ├── guia_presentacion.txt
 └── .gitignore

Ejemplo de Uso
Solicitud:

POST → /api/analisis-completo

"Demien Becerra viajó al ITESO en Guadalajara para discutir sobre innovación y tecnología educativa. 
Los expertos calificaron la reunión como excelente para la investigación universitaria."

Respuesta esperada:
{
  "sentimiento": {
    "totalPalabras": 27,
    "positivas": ["excelente"],
    "negativas": [],
    "sentimiento": "positivo"
  },
  "entidades": {
    "PERSON": ["Demien Becerra"],
    "LOCATION": ["Guadalajara"],
    "ORGANIZATION": ["ITESO"]
  },
  "resumenClasico": [
    "Los expertos calificaron la reunión como excelente para la investigación universitaria."
  ],
  "temasDetectados": [
    {
      "tema": 1,
      "palabrasClave": ["innovación", "educación", "tecnología"]
    }
  ],
  "resumenGlobal": [
    "Los expertos calificaron la reunión como excelente para la investigación universitaria."
  ],
  "longitudTexto": 27,
  "versionModelo": "TextMinerDBL v6.0",
  "autor": "Demien Becerra Lozano"
}

Instrucciones de Ejecución

Abrir el proyecto en Eclipse o IntelliJ.

Ejecutar la clase principal: TextminerDblApplication.java.

Esperar el mensaje en consola:

Tomcat started on port 8080


Probar los endpoints desde Postman o el navegador:

http://localhost:8080/api/analisis-completo

http://localhost:8080/api/historial

Visualizar el dashboard:

http://localhost:8080/

Dependencias Principales (pom.xml)
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <dependency>
        <groupId>org.apache.opennlp</groupId>
        <artifactId>opennlp-tools</artifactId>
        <version>2.3.1</version>
    </dependency>

    <dependency>
        <groupId>cc.mallet</groupId>
        <artifactId>mallet</artifactId>
        <version>2.0.8</version>
    </dependency>

    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.17.0</version>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
    </dependency>
</dependencies>

Documentación Técnica para Presentación

Arquitectura: API modular con servicios independientes conectados a controladores REST.

Lógica principal:

Procesamiento lingüístico con OpenNLP.

Modelado temático probabilístico con MALLET (LDA).

Resumen automático basado en frecuencia y cobertura temática.

Persistencia: los resultados se guardan en JSON bajo /resultados/.

Dashboard: interfaz estática en resources/static/index.html conectada a la API vía Fetch.

Objetivo final: analizar texto de manera automática, identificar temas, sentimiento y generar reportes legibles.

Autores y Créditos

Proyecto desarrollado por Demien Becerra Lozano
Facultad de Ingeniería en Ciencia de Datos – ITESO (2025)