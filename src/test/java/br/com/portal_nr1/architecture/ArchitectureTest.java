package br.com.portal_nr1.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "br.com.portal_nr1", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

	private static final String DOMAIN = "..domain..";
	private static final String APPLICATION = "..application..";
	private static final String INFRASTRUCTURE = "..infrastructure..";

	@ArchTest
	static final ArchRule domainShouldNotDependOnOtherLayers = noClasses()
		.that()
		.resideInAnyPackage(DOMAIN)
		.should()
		.dependOnClassesThat()
		.resideInAnyPackage(APPLICATION, INFRASTRUCTURE);

	@ArchTest
	static final ArchRule applicationShouldNotDependOnInfrastructure = noClasses()
		.that()
		.resideInAnyPackage(APPLICATION)
		.should()
		.dependOnClassesThat()
		.resideInAnyPackage(INFRASTRUCTURE);

	@Test
	@DisplayName("Dependencies flow inward to domain/application")
	void layersPointTowardsCenter() {
		var classes = new ClassFileImporter()
			.withImportOption(new ImportOption.DoNotIncludeTests())
			.importPackages("br.com.portal_nr1");

		onionArchitecture()
			.domainModels(DOMAIN)
			.domainServices(DOMAIN)
			.applicationServices(APPLICATION)
			.adapter("infrastructure", INFRASTRUCTURE)
			.check(classes);
	}
}