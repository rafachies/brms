package org.rchies.bpm.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;

public class ClasspathUtils {

	@Inject
	private Logger logger;
	
	
	public List<String> getProcessesDefinitions() {
		Properties properties = System.getProperties();
		Set<Object> keySet = properties.keySet();
		for (Object key : keySet) {
			System.out.println(key + " - " + properties.get(key));
		}
		
		
		try {
			String paths = System.getProperty("java.class.path");
			List<String> bpmnFiles = new ArrayList<String>();
			for (final String path : paths.split(File.pathSeparator)) {
				final File file = new File(path);
				if( file.isDirectory()) {
					recurse(bpmnFiles, file);
				}
				else {
					String canonicalPath = file.getCanonicalPath();
					if (canonicalPath.endsWith(".bpmn")) {
						bpmnFiles.add(file.getCanonicalPath().split("/classes")[1]);
					}
				}
			}
			return bpmnFiles;
		} catch (Exception e) {
			logger.error("Error while reading bpmn2 files from classpath", e);
			throw new RuntimeException(e);
		}
	}
	private void recurse(List<String> bpmnFiles, File f) throws Exception { 
		File list[] = f.listFiles();
		for (File file : list) {
			if (file.isDirectory()) {
				recurse(bpmnFiles, file);
			} else {
				String canonicalPath = file.getCanonicalPath();
				if (canonicalPath.endsWith(".bpmn2")) {
					bpmnFiles.add(file.getCanonicalPath().split("/classes/")[1]);
				}
			}
		}
	}
}