# PerConIK - Interactive visualization of developer‘s actions at software product development

Lukas Sekerak's diploma thesis solution.

## Project idea

Software product development provokes numerous unanswered questions which may not be easily resolved. Many visualization techniques do not offer the option to view the development process from the perspective of particular steps of a developer.  

Step-by-step approach enables visualisation of various actions taken during the process – such as utilization of a browser or communication/informationchannels for trouble-shooting etc. Another significant indicator to be mindful of is transcription of own or externally acquired code. These actions are parts of software product development and their visualization may answer questions such as: “If a developer uses a browser (a particular portal) frequently during code-writing, how often is the code rewritten? Does this tendency apply to all of his source codes? If so, does this apply to every other developer in the same manner?”

## Requirements

- Java 1.7
- Maven 3.2
- Apache Tomcat 7.0.54 (to run)

## Setup

1. Clone repository into workspace
2. Download external maven libraries
3. Build project
4. Edit project's configuration properties at file 'conf/configuration.xml'
4. Deploy artifact to server
5. Set VM option Dconfig.dir to configuration directory.
    Example: -Dconfig.dir=D:\workspace\sk.stuba.fiit.perconik.ivda\conf

## Configuration directory content
- configuration.xml - Project configuration file  
- processBlackList.txt - List of black listed processes  
- log4j.properties - Log4j configuration file
  
## Acknowledgement

This Software is the partial result of the Research and Development
Operational Programme for the project Research of methods for acquisition,
analysis and personalized conveying of information and knowledge,
ITMS 26240220039, co-funded by the ERDF.

## License

This software is released under the [MIT License](LICENSE.md).

## Credits
- Jos de Jong, Almende B.V. - Javascript library [timeline-2.8.0](http://almende.github.io/chap-links-library/timeline.html)
- Pavol Zbell - Java consultant 