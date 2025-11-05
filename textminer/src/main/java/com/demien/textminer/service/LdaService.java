package com.demien.textminer.service;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.*;
import cc.mallet.topics.*;
import cc.mallet.types.*;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

@Service
public class LdaService {

    public Map<String, Object> analizarTemas(String texto) throws Exception {
        Map<String, Object> resultado = new LinkedHashMap<>();

        // === 1️⃣ Crear archivo temporal con el texto ===
        File tempFile = File.createTempFile("lda_input", ".txt");
        try (FileWriter fw = new FileWriter(tempFile)) {
            fw.write(texto);
        }

        // === 2️⃣ Crear pipeline de procesamiento de texto ===
        ArrayList<Pipe> pipeList = new ArrayList<>();
        pipeList.add(new Input2CharSequence("UTF-8"));
        pipeList.add(new CharSequenceLowercase());
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        pipeList.add(new TokenSequenceRemoveStopwords(
        		new File(getClass().getResource("/stoplists/en.txt").toURI()),
                "UTF-8", false, false, false));
        pipeList.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipeList));
        Reader fileReader = new InputStreamReader(new FileInputStream(tempFile), "UTF-8");
        instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^(.*)$"), 1, -1, -1));

        // === 3️⃣ Crear modelo LDA ===
        int numTopics = 3; // número de temas a detectar
        ParallelTopicModel model = new ParallelTopicModel(numTopics);
        model.addInstances(instances);
        model.setNumThreads(2);
        model.setNumIterations(1000);
        model.estimate();

        // === 4️⃣ Obtener palabras clave por tema ===
        Alphabet dataAlphabet = instances.getDataAlphabet();
        ArrayList<TreeSet<IDSorter>> topicSortedWords = model.getSortedWords();

        List<Map<String, Object>> temas = new ArrayList<>();

        for (int topic = 0; topic < numTopics; topic++) {
            Map<String, Object> tema = new LinkedHashMap<>();
            List<String> palabras = new ArrayList<>();

            Iterator<IDSorter> iterator = topicSortedWords.get(topic).iterator();
            int count = 0;
            while (iterator.hasNext() && count < 10) {
                IDSorter idCountPair = iterator.next();
                palabras.add((String) dataAlphabet.lookupObject(idCountPair.getID()));
                count++;
            }

            tema.put("tema", topic + 1);
            tema.put("palabrasClave", palabras);
            temas.add(tema);
        }

        resultado.put("temasDetectados", temas);

        // Eliminar archivo temporal
        tempFile.delete();

        return resultado;
    }
}