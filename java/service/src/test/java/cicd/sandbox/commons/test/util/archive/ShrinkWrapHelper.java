package cicd.sandbox.commons.test.util.archive;

import java.io.File;

import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.container.LibraryContainer;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
import org.jboss.shrinkwrap.resolver.api.maven.ScopeType;
import org.jboss.shrinkwrap.resolver.api.maven.strategy.RejectDependenciesStrategy;

/**
 * ShrinkWrapのヘルパークラスです.
 * <p>
 * アプリケーションに必要な依存ライブラリを同梱したWeb Applicaiotn Archive(war)
 * またはEnterprise Archive(ear)を作成を補助するユーティリティメソッドを提供しています。
 * </p>
 *
 */
public abstract class ShrinkWrapHelper {

	private static final String DEFAULT_POM_PATH = "pom.xml";
	
	private static final ScopeType[] COMPILE_SCOPE = {ScopeType.COMPILE};
	
	private static final String[] EMPTY_ARRAY = new String[0];
	
	/**
	 * アプリケーションに必要な依存ライブラリを含んだEnterprise Archiveを作成します.
	 * <p>
	 * 依存ライブラリの解決は引数 <code>earPomPath</code>で渡されたpomのcompile
	 * スコープの依存ライブラリとそのライブラリが依存しているライブラリを全て取得し、アーカイブに追加します。
	 * </p>
	 * 
	 * @param earName earアーカイブ名 "app.ear"のような拡張子を含んだアーカイブ名を指定してください
	 * @param earPomPath earのpom.xmlのパス
	 * @return Enterprise Archive
	 */
	public static EnterpriseArchive enterpriseArchiveWithLibs(String earName, String earPomPath) {
		return archiveWithLibs(EnterpriseArchive.class, earName, earPomPath, EMPTY_ARRAY);
	}
	
	/**
	 * アプリケーションに必要な依存ライブラリを含んだEnterprise Archiveを作成します.
	 * <p>
	 * 依存ライブラリの解決は引数 <code>earPomPath</code>で渡されたpomのcompile
	 * スコープの依存ライブラリとそのライブラリが依存しているライブラリを全て取得し、アーカイブに追加します。<br>
	 * <code>earPomPath</code>はテスト実行クラスのプロジェクト以下(プロジェクトのpom.xmlの配置場所)が起点となる相対パスか絶対パスを指定してください。
	 * </p>
	 * 
	 * <p>
	 * <code>earPomPath</code>がテストターゲットとなるアーカイブの依存を含んでいる場合、
	 * テストターゲットの依存を除外してあげる必要があります。<br>
	 * その際は<code>excludeArtifacts</code>の引数に除外対象のアーティファクトを指定することでアーカイブを除外することができます。<br>
	 * <code>excludeArtifacts</code>に渡す文字列は"groupId:artifactId:version"のようなShrinkWrap Resolverのアーティファクトの指定方法になります。
	 * </p>
	 * 
	 * @param earName earアーカイブ名 "app.ear"のような拡張子を含んだアーカイブ名を指定してください
	 * @param earPomPath earのpom.xmlのパス nullを
	 * @param excludeArtifacts 除外対象のアーティファクト
	 * @return Enterprise Archive
	 * 
	 * @see https://github.com/shrinkwrap/resolver ShrinkWrap Resolver
	 */
	public static EnterpriseArchive enterpriseArchiveWithLibs(String earName, String earPomPath, String... excludeArtifacts) {
		return archiveWithLibs(EnterpriseArchive.class, earName, earPomPath, excludeArtifacts);
	}
	
	/**
	 * アプリケーションに必要な依存ライブラリを含んだWeb Application Archiveを作成します.
	 * <p>
	 * 依存ライブラリの解決は引数 <code>warPomPath</code>で渡されたpomのcompile
	 * スコープの依存ライブラリとそのライブラリが依存しているライブラリを全て取得し、アーカイブに追加します。<br>
	 * <code>earPomPath</code>はテスト実行クラスのプロジェクト以下(プロジェクトのpom.xmlの配置場所)が起点となる相対パスか絶対パスを指定してください。
	 * </p>
	 * 
	 * @param warName warアーカイブ名 "app.war"のような拡張子を含んだアーカイブ名を指定してください
	 * @param warPomPath warのpom.xmlのパス
	 * 
	 * @return Web Application Archive
	 */
	public static WebArchive webArchiveWithLibs(String warName, String warPomPath) {
		return archiveWithLibs(WebArchive.class, warName, warPomPath, EMPTY_ARRAY);
	}

	/**
	 * アプリケーションに必要な依存ライブラリを含んだWeb Application Archiveを作成します.
	 * <p>
	 * 依存ライブラリの解決は引数 <code>warPomPath</code>で渡されたpomのcompile
	 * スコープの依存ライブラリとそのライブラリが依存しているライブラリを全て取得し、アーカイブに追加します。<br>
	 * <code>earPomPath</code>はテスト実行クラスのプロジェクト以下(プロジェクトのpom.xmlの配置場所)が起点となる相対パスか絶対パスを指定してください。
	 * </p>
	 * 
	 * <p>
	 * <code>warPomPath</code>がテストターゲットとなるアーカイブの依存を含んでいる場合、
	 * テストターゲットの依存を除外してあげる必要があります。<br>
	 * その際は<code>excludeArtifacts</code>の引数に除外対象のアーティファクトを指定することでアーカイブを除外することができます。<br>
	 * <code>excludeArtifacts</code>に渡す文字列は"groupId:artifactId:version"のようなShrinkWrap Resolverのアーティファクトの指定方法になります。
	 * </p>
	 * 
	 * @param warName warアーカイブ名 "app.war"のような拡張子を含んだアーカイブ名を指定してください
	 * @param warPomPath warのpom.xmlのパス
	 * @param excludeArtifacts 除外対象のアーティファクト
	 * 
	 * @return Web Application Archive
	 */
	public static WebArchive webArchiveWithLibs(String warName, String warPomPath, String... excludeArtifacts) {
		return archiveWithLibs(WebArchive.class, warName, warPomPath, excludeArtifacts);
	}
	
	/**
	 * アプリケーションに必要な依存ライブラリを含んだArchiveを作成します.
	 * <p>
	 * 依存ライブラリの解決は引数 <code>archivePomPath</code>で渡されたpomのcompile
	 * スコープの依存ライブラリとそのライブラリが依存しているライブラリを全て取得し、アーカイブに追加します。<br>
	 * <code>earPomPath</code>はテスト実行クラスのプロジェクト以下(プロジェクトのpom.xmlの配置場所)が起点となる相対パスか絶対パスを指定してください。
	 * </p>
	 * 
	 * <p>
	 * <code>archivePomPath</code>がテストターゲットとなるアーカイブの依存を含んでいる場合、
	 * テストターゲットの依存を除外してあげる必要があります。<br>
	 * その際は<code>excludeArtifacts</code>の引数に除外対象のアーティファクトを指定することでアーカイブを除外することができます。<br>
	 * <code>excludeArtifacts</code>に渡す文字列は"groupId:artifactId:version"のようなShrinkWrap Resolverのアーティファクトの指定方法になります。
	 * </p>
	 * 
	 * @param targtArchive ターゲットのアーカイブ
	 * @param archiveName アーカイブ名 "app.war"のような拡張子を含んだアーカイブ名を指定してください
	 * @param archivePomPath pom.xmlのパス
	 * @param excludeArtifacts 除外対象のアーティファクト
	 * @return アーカイブ
	 */
	public static <T extends Archive<T>> T archiveWithLibs(Class<T> targtArchive, String archiveName, String archivePomPath, String... excludeArtifacts) {
		T archive = ShrinkWrap.create(targtArchive, archiveName);
		archive = addCompileScopeLibsTo(archive, archivePomPath, excludeArtifacts);
		return archive;
	}
	
	/**
	 * アーカイブにpom.xmlから読み込んだcompileスコープの依存ライブラリを追加します。
	 * <p>
	 * 依存ライブラリのアーティファクトを除外する際は<code>excludeArtifacts</code>の引数に除外対象のアーティファクトを指定することでアーカイブを除外することができます。<br>
	 * <code>excludeArtifacts</code>に渡す文字列は"groupId:artifactId:version"のようなShrinkWrap Resolverのアーティファクトの指定方法になります。
	 * </p>
	 * @param archive 依存ライブラリを追加するアーカイブ
	 * @param excludeArtifacts 除外対象のアーティファクト
	 * @return
	 */
	public static <T extends Archive<T>> T addCompileScopeLibsTo(T archive, String... excludeArtifacts) {
		return addCompileScopeLibsTo(archive, DEFAULT_POM_PATH, excludeArtifacts);
	}
	
	/**
	 * アーカイブにpom.xmlから読み込んだcompileスコープの依存ライブラリを追加します。
	 * <p>
	 * 依存ライブラリのアーティファクトを除外する際は<code>excludeArtifacts</code>の引数に除外対象のアーティファクトを指定することでアーカイブを除外することができます。<br>
	 * <code>excludeArtifacts</code>に渡す文字列は"groupId:artifactId:version"のようなShrinkWrap Resolverのアーティファクトの指定方法になります。
	 * </p>
	 * @param archive 依存ライブラリを追加するアーカイブ
	 * @param pomPath pom.xmlのパス
	 * @param excludeArtifacts 除外対象のアーティファクト
	 * @return
	 */
	public static <T extends Archive<T>> T addCompileScopeLibsTo(T archive, String pomPath, String... excludeArtifacts) {
		pomPath = isNotNullOrEmptyString(pomPath) ? pomPath : DEFAULT_POM_PATH;
		if (archive instanceof LibraryContainer) {
			@SuppressWarnings("unchecked")
			LibraryContainer<T> libraryContainer = (LibraryContainer<T>) archive;
			libraryContainer.addAsLibraries(loadLibsFromPom(pomPath, COMPILE_SCOPE ,excludeArtifacts));
		} else {
			throw new IllegalArgumentException("引数で指定されたアーカイブはライブラリを追加できないアーカイブです。");
		}
		
		return archive;
	}
	
	/**
	 * pom.xmlから依存するアーティファクト(アーカイブ)を取得します。
	 * <p>
	 * 依存ライブラリのアーティファクトを除外する際は<code>excludeArtifacts</code>の引数に除外対象のアーティファクトを指定することでアーカイブを除外することができます。<br>
	 * <code>excludeArtifacts</code>に渡す文字列は"groupId:artifactId:version"のようなShrinkWrap Resolverのアーティファクトの指定方法になります。
	 * </p>
	 * @param pomPath pom.xmlのパス
	 * @param scope スコープ
	 * @param excludeArtifacts 除外対象のアーティファクト
	 * @return アーカイブファイルの配列
	 */
	public static File[] loadLibsFromPom(String pomPath,ScopeType[] scope, String... excludeArtifacts) {
		MavenStrategyStage mavenStrategy = loadPom(pomPath, scope);
		
		if (excludeArtifacts == null || excludeArtifacts.length == 0) {
			return mavenStrategy.withTransitivity().asFile();
		} else {
			return mavenStrategy.using(new RejectDependenciesStrategy(false, excludeArtifacts)).asFile();
		}
		
	}

	private static MavenStrategyStage loadPom(String pomPath, ScopeType[] type) {
		MavenStrategyStage mavenStrategy = 
				Maven.resolver()
				.loadPomFromFile(pomPath)
				.importDependencies(type)
				.resolve();
		return mavenStrategy;
	}
	
	
	private static boolean isNotNullOrEmptyString(String value) {
		return value != null && !"".equals(value.trim());
	}
}
