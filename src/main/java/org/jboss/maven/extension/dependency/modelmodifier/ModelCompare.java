package org.jboss.maven.extension.dependency.modelmodifier;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.model.Activation;
import org.apache.maven.model.ActivationFile;
import org.apache.maven.model.ActivationOS;
import org.apache.maven.model.ActivationProperty;
import org.apache.maven.model.Build;
import org.apache.maven.model.BuildBase;
import org.apache.maven.model.CiManagement;
import org.apache.maven.model.Contributor;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.DeploymentRepository;
import org.apache.maven.model.Developer;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Exclusion;
import org.apache.maven.model.IssueManagement;
import org.apache.maven.model.License;
import org.apache.maven.model.MailingList;
import org.apache.maven.model.Model;
import org.apache.maven.model.Notifier;
import org.apache.maven.model.Organization;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.model.PluginManagement;
import org.apache.maven.model.Prerequisites;
import org.apache.maven.model.Profile;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.ReportPlugin;
import org.apache.maven.model.ReportSet;
import org.apache.maven.model.Reporting;
import org.apache.maven.model.Repository;
import org.apache.maven.model.RepositoryPolicy;
import org.apache.maven.model.Resource;
import org.apache.maven.model.Scm;
import org.apache.maven.model.Site;

/**
 * Compares Models and data in their public fields.
 */
public class ModelCompare
{
    private static final int DEBUG = Integer.valueOf( "0" );

    private static boolean areConfigurationEqual( Object config1, Object config2 )
    {
        return eitherIsNull( config1, config2 ) ? bothAreNull( config1, config2 ) : config1.equals( config2 );
    }

    public static boolean areContributorsEqual( List<Contributor> contributors1, List<Contributor> contributors2 )
    {
        if ( eitherIsNull( contributors1, contributors2 ) )
            return bothAreNull( contributors1, contributors2 );
        else if ( contributors1.size() != contributors2.size() )
            return false;

        for ( int i = 0; i < contributors1.size(); i++ )
        {
            Contributor e1 = contributors1.get( i );
            Contributor e2 = contributors2.get( i );

            if ( !areEqual( e1.getEmail(), e2.getEmail() ) )
                return false;
            else if ( !areEqual( e1.getName(), e2.getName() ) )
                return false;
            else if ( !areEqual( e1.getOrganization(), e2.getOrganization() ) )
                return false;
            else if ( !areEqual( e1.getOrganizationUrl(), e2.getOrganizationUrl() ) )
                return false;
            else if ( !areEqual( e1.getProperties(), e2.getProperties() ) )
                return false;
            else if ( !areEqual( e1.getRoles(), e2.getRoles() ) )
                return false;
            else if ( !areEqual( e1.getTimezone(), e2.getTimezone() ) )
                return false;
            else if ( !areEqual( e1.getUrl(), e2.getUrl() ) )
                return false;
        }

        return true;
    }

    /**
     * Dependency does not implement a true equality method, so we need to compare lists of that ourselves
     */
    public static boolean areDependenciesEqual( List<Dependency> list1, List<Dependency> list2 )
    {
        if ( eitherIsNull( list1, list2 ) )
            return bothAreNull( list1, list2 );
        else if ( list1.size() != list2.size() )
            return false;

        for ( int i = 0; i < list1.size(); i++ )
        {
            Dependency e1 = list1.get( i );
            Dependency e2 = list2.get( i );

            if ( !areEqual( e1.getArtifactId(), e2.getArtifactId() ) )
                return false;
            else if ( !areEqual( e1.getClassifier(), e2.getClassifier() ) )
                return false;
            else if ( !areExclusionsEqual( e1.getExclusions(), e2.getExclusions() ) )
                return false;
            else if ( !areEqual( e1.getGroupId(), e2.getGroupId() ) )
                return false;
            else if ( !areEqual( e1.getManagementKey(), e2.getManagementKey() ) )
                return false;
            else if ( !areEqual( e1.getOptional(), e2.getOptional() ) )
                return false;
            else if ( !areEqual( e1.getScope(), e2.getScope() ) )
                return false;
            else if ( !areEqual( e1.getSystemPath(), e2.getSystemPath() ) )
                return false;
            else if ( !areEqual( e1.getType(), e2.getType() ) )
                return false;
            else if ( !areEqual( e1.getVersion(), e2.getVersion() ) )
                return false;
        }

        return true;
    }

    public static boolean areDevelopersEqual( List<Developer> developers1, List<Developer> developers2 )
    {
        if ( eitherIsNull( developers1, developers2 ) )
            return bothAreNull( developers1, developers2 );
        else if ( developers1.size() != developers2.size() )
            return false;

        for ( int i = 0; i < developers1.size(); i++ )
        {
            Developer e1 = developers1.get( i );
            Developer e2 = developers2.get( i );

            if ( !areEqual( e1.getEmail(), e2.getEmail() ) )
                return false;
            else if ( !areEqual( e1.getId(), e2.getId() ) )
                return false;
            else if ( !areEqual( e1.getName(), e2.getName() ) )
                return false;
            else if ( !areEqual( e1.getOrganization(), e2.getOrganization() ) )
                return false;
            else if ( !areEqual( e1.getOrganizationUrl(), e2.getOrganizationUrl() ) )
                return false;
            else if ( !areEqual( e1.getProperties(), e2.getProperties() ) )
                return false;
            else if ( !areEqual( e1.getRoles(), e2.getRoles() ) )
                return false;
            else if ( !areEqual( e1.getTimezone(), e2.getTimezone() ) )
                return false;
            else if ( !areEqual( e1.getUrl(), e2.getUrl() ) )
                return false;
        }

        return true;
    }

    private static boolean areEqual( Activation activation1, Activation activation2 )
    {
        if ( eitherIsNull( activation1, activation2 ) )
            return bothAreNull( activation1, activation2 );
        else if ( !areEqual( activation1.getJdk(), activation2.getJdk() ) )
            return false;
        else if ( !areEqual( activation1.getFile(), activation2.getFile() ) )
            return false;
        else if ( !areEqual( activation1.getOs(), activation2.getOs() ) )
            return false;
        else if ( !areEqual( activation1.getProperty(), activation2.getProperty() ) )
            return false;
        else
            return true;
    }

    private static boolean areEqual( ActivationFile file1, ActivationFile file2 )
    {
        if ( eitherIsNull( file1, file2 ) )
            return bothAreNull( file1, file2 );
        else if ( !areEqual( file1.getExists(), file2.getExists() ) )
            return false;
        else if ( !areEqual( file1.getMissing(), file2.getMissing() ) )
            return false;
        else
            return true;
    }

    private static boolean areEqual( ActivationOS os1, ActivationOS os2 )
    {
        if ( eitherIsNull( os1, os2 ) )
            return bothAreNull( os1, os2 );
        else if ( !areEqual( os1.getArch(), os2.getArch() ) )
            return false;
        else if ( !areEqual( os1.getFamily(), os2.getFamily() ) )
            return false;
        else if ( !areEqual( os1.getName(), os2.getName() ) )
            return false;
        else if ( !areEqual( os1.getVersion(), os2.getVersion() ) )
            return false;
        else
            return true;
    }

    private static boolean areEqual( ActivationProperty property1, ActivationProperty property2 )
    {
        if ( eitherIsNull( property1, property2 ) )
            return bothAreNull( property1, property2 );
        else if ( !areEqual( property1.getName(), property2.getName() ) )
            return false;
        else if ( !areEqual( property1.getValue(), property2.getValue() ) )
            return false;
        else
            return true;
    }

    public static boolean areEqual( Build build1, Build build2 )
    {
        if ( eitherIsNull( build1, build2 ) )
            return bothAreNull( build1, build2 );
        else if ( !areEqual( build1.getDefaultGoal(), build2.getDefaultGoal() ) )
            return false;
        else if ( !areEqual( build1.getDirectory(), build2.getDirectory() ) )
            return false;
        else if ( !areEqual( build1.getExtensions(), build2.getExtensions() ) ) // Impls. Eq.
            return false;
        else if ( !areEqual( build1.getFilters(), build2.getFilters() ) )
            return false;
        else if ( !areEqual( build1.getFinalName(), build2.getFinalName() ) )
            return false;
        else if ( !areEqual( build1.getOutputDirectory(), build2.getOutputDirectory() ) )
            return false;
        else if ( !arePluginsEqual( build1.getPlugins(), build2.getPlugins() ) )
            return false;
        else if ( !areResourcesEqual( build1.getResources(), build2.getResources() ) )
            return false;
        else if ( !areEqual( build1.getScriptSourceDirectory(), build2.getScriptSourceDirectory() ) )
            return false;
        else if ( !areEqual( build1.getSourceDirectory(), build2.getSourceDirectory() ) )
            return false;
        else if ( !areEqual( build1.getTestOutputDirectory(), build2.getTestOutputDirectory() ) )
            return false;
        else if ( !areEqual( build1.getTestSourceDirectory(), build2.getTestSourceDirectory() ) )
            return false;
        else if ( !areEqual( build1.getPluginManagement(), build2.getPluginManagement() ) )
            return false;
        else
            return true;
    }

    private static boolean areEqual( BuildBase build1, BuildBase build2 )
    {
        if ( eitherIsNull( build1, build2 ) )
            return bothAreNull( build1, build2 );
        else if ( !areEqual( build1.getDefaultGoal(), build2.getDefaultGoal() ) )
            return false;
        else if ( !areEqual( build1.getDirectory(), build2.getDirectory() ) )
            return false;
        else if ( !areEqual( build1.getFilters(), build2.getFilters() ) )
            return false;
        else if ( !areEqual( build1.getFinalName(), build2.getFinalName() ) )
            return false;
        else if ( !areEqual( build1.getPluginManagement(), build2.getPluginManagement() ) )
            return false;
        else if ( !arePluginsEqual( build1.getPlugins(), build2.getPlugins() ) )
            return false;
        else if ( !areResourcesEqual( build1.getResources(), build2.getResources() ) )
            return false;
        else if ( !areResourcesEqual( build1.getTestResources(), build2.getTestResources() ) )
            return false;
        else
            return true;
    }

    public static boolean areEqual( CiManagement ciManagement1, CiManagement ciManagement2 )
    {
        if ( eitherIsNull( ciManagement1, ciManagement2 ) )
            return bothAreNull( ciManagement1, ciManagement2 );
        else if ( !areNotifiersEqual( ciManagement1.getNotifiers(), ciManagement2.getNotifiers() ) )
            return false;
        else if ( !areEqual( ciManagement1.getSystem(), ciManagement2.getSystem() ) )
            return false;
        else if ( !areEqual( ciManagement1.getUrl(), ciManagement2.getUrl() ) )
            return false;
        else
            return true;
    }

    public static boolean areEqual( DependencyManagement dependencyManagement1,
                                    DependencyManagement dependencyManagement2 )
    {
        if ( eitherIsNull( dependencyManagement1, dependencyManagement2 ) )
            return bothAreNull( dependencyManagement1, dependencyManagement2 );
        else if ( !areDependenciesEqual( dependencyManagement1.getDependencies(),
                                         dependencyManagement2.getDependencies() ) )
            return false;
        else
            return true;
    }

    private static boolean areEqual( DeploymentRepository repository1, DeploymentRepository repository2 )
    {
        if ( eitherIsNull( repository1, repository2 ) )
            return bothAreNull( repository1, repository2 );
        else if ( !areEqual( repository1.getId(), repository2.getId() ) )
            return false;
        else if ( !areEqual( repository1.getLayout(), repository2.getLayout() ) )
            return false;
        else if ( !areEqual( repository1.getName(), repository2.getName() ) )
            return false;
        else if ( !areEqual( repository1.getUrl(), repository2.getUrl() ) )
            return false;
        else if ( !areEqual( repository1.getReleases(), repository2.getReleases() ) )
            return false;
        else if ( !areEqual( repository1.getSnapshots(), repository2.getSnapshots() ) )
            return false;
        else
            return true;
    }

    public static boolean areEqual( DistributionManagement distributionManagement1,
                                    DistributionManagement distributionManagement2 )
    {
        if ( eitherIsNull( distributionManagement1, distributionManagement2 ) )
            return bothAreNull( distributionManagement1, distributionManagement2 );
        else if ( !areEqual( distributionManagement1.getDownloadUrl(), distributionManagement2.getDownloadUrl() ) )
            return false;
        else if ( !areEqual( distributionManagement1.getStatus(), distributionManagement2.getStatus() ) )
            return false;
        else if ( !areEqual( distributionManagement1.getRelocation(), distributionManagement2.getRelocation() ) )
            return false;
        else if ( !areEqual( distributionManagement1.getRepository(), distributionManagement2.getRepository() ) )
            return false;
        else if ( !areEqual( distributionManagement1.getSite(), distributionManagement2.getSite() ) )
            return false;
        else if ( !areEqual( distributionManagement1.getSnapshotRepository(),
                             distributionManagement2.getSnapshotRepository() ) )
            return false;
        else
            return true;
    }

    private static boolean areEqual( File file1, File file2 )
    {
        return eitherIsNull( file1, file2 ) ? bothAreNull( file1, file2 ) : file1.equals( file2 );
    }

    public static boolean areEqual( IssueManagement issueManagement1, IssueManagement issueManagement2 )
    {
        if ( eitherIsNull( issueManagement1, issueManagement2 ) )
            return bothAreNull( issueManagement1, issueManagement2 );
        else if ( !areEqual( issueManagement1.getSystem(), issueManagement2.getSystem() ) )
            return false;
        else if ( !areEqual( issueManagement1.getUrl(), issueManagement2.getUrl() ) )
            return false;
        else
            return true;
    }

    /**
     * Works for all Lists that contain objects whose class implements a true equality method for equals()
     */
    public static boolean areEqual( List<?> list1, List<?> list2 )
    {
        // Can't get more specific due to type erasure. The List interface specifies a proper equality method, but that
        // doesn't guarantee that the contained objects don't implement identicalness.
        return eitherIsNull( list1, list2 ) ? bothAreNull( list1, list2 ) : list1.equals( list2 );
    }

    /**
     * Check equality of two Models (true equality, not identicalness).
     * 
     * @param o1 First Model
     * @param o2 Second Model
     * @return true iff the Models' public fields are equal
     */
    public static boolean areEqual( Model o1, Model o2 )
    {
        // We can only compare properly if both objects aren't null
        if ( eitherIsNull( o1, o2 ) ) {
            boolean retVal = bothAreNull( o1, o2 );
            if (!retVal)
                recordFailReason( "0" );
            return retVal;
        }

        // We can only compare field pairs that both aren't null
        // "Easy" checks (Objects with proper equality methods built-in)
        else if ( !areEqual( o1.getArtifactId(), o2.getArtifactId() ) )
            recordFailReason( "1" );
        else if ( !areEqual( o1.getDescription(), o2.getDescription() ) )
            recordFailReason( "2" );
        else if ( !areEqual( o1.getGroupId(), o2.getGroupId() ) )
            recordFailReason( "3" );
        else if ( !areEqual( o1.getId(), o2.getId() ) )
            recordFailReason( "4" );
        else if ( !areEqual( o1.getInceptionYear(), o2.getInceptionYear() ) )
            recordFailReason( "5" );
        else if ( !areEqual( o1.getModelEncoding(), o2.getModelEncoding() ) )
            recordFailReason( "6" );
        else if ( !areEqual( o1.getModelVersion(), o2.getModelVersion() ) )
            recordFailReason( "7" );
        else if ( !areEqual( o1.getName(), o2.getName() ) )
            recordFailReason( "8" );
        else if ( !areEqual( o1.getPackaging(), o2.getPackaging() ) )
            recordFailReason( "9" );
        else if ( !areEqual( o1.getPomFile(), o2.getPomFile() ) )
            recordFailReason( "10" );
        else if ( !areEqual( o1.getProjectDirectory(), o2.getProjectDirectory() ) )
            recordFailReason( "11" );
        else if ( !areEqual( o1.getUrl(), o2.getUrl() ) )
            recordFailReason( "12" );
        else if ( !areEqual( o1.getVersion(), o2.getVersion() ) )
            recordFailReason( "13" );

        // Harder, nested checks (Objects with identicalness methods instead of equality)
        else if ( !areEqual( o1.getBuild(), o2.getBuild() ) )
            recordFailReason( "14" );
        else if ( !areEqual( o1.getCiManagement(), o2.getCiManagement() ) )
            recordFailReason( "15" );
        else if ( !areEqual( o1.getParent(), o2.getParent() ) )
            recordFailReason( "16" );
        else if ( !areContributorsEqual( o1.getContributors(), o2.getContributors() ) )
            recordFailReason( "17" );
        else if ( !areDependenciesEqual( o1.getDependencies(), o2.getDependencies() ) )
            recordFailReason( "18" );
        else if ( !areEqual( o1.getDependencyManagement(), o2.getDependencyManagement() ) )
            recordFailReason( "19" );
        else if ( !areDevelopersEqual( o1.getDevelopers(), o2.getDevelopers() ) )
            recordFailReason( "20" );
        else if ( !areEqual( o1.getDistributionManagement(), o2.getDistributionManagement() ) )
            recordFailReason( "21" );
        else if ( !areEqual( o1.getIssueManagement(), o2.getIssueManagement() ) )
            recordFailReason( "22" );
        else if ( !areLicensesEqual( o1.getLicenses(), o2.getLicenses() ) )
            recordFailReason( "23" );
        else if ( !areMailingListsEqual( o1.getMailingLists(), o2.getMailingLists() ) )
            recordFailReason( "24" );
        else if ( !areEqual( o1.getModules(), o2.getModules() ) )
            recordFailReason( "25" );
        else if ( !areEqual( o1.getOrganization(), o2.getOrganization() ) )
            recordFailReason( "26" );
        else if ( !areRepositoriesEqual( o1.getPluginRepositories(), o2.getPluginRepositories() ) )
            recordFailReason( "27" );
        else if ( !areEqual( o1.getPrerequisites(), o2.getPrerequisites() ) )
            recordFailReason( "28" );
        else if ( !areProfilesEqual( o1.getProfiles(), o2.getProfiles() ) )
            recordFailReason( "29" );
        else if ( !areEqual( o1.getProperties(), o2.getProperties() ) )
            recordFailReason( "30" );
        else if ( !areEqual( o1.getReporting(), o2.getReporting() ) )
            recordFailReason( "31" );
        else if ( !areRepositoriesEqual( o1.getRepositories(), o2.getRepositories() ) )
            recordFailReason( "32" );
        else if ( !areEqual( o1.getScm(), o2.getScm() ) )
            recordFailReason( "33" );

        // Everything passed
        else
            return true;

        // Something didn't pass (comes here after going through the method call)
        return false;
    }

    public static boolean areEqual( Organization organization1, Organization organization2 )
    {
        if ( eitherIsNull( organization1, organization2 ) )
            return bothAreNull( organization1, organization2 );
        else if ( !areEqual( organization1.getName(), organization2.getName() ) )
            return false;
        else if ( !areEqual( organization1.getUrl(), organization2.getUrl() ) )
            return false;
        else
            return true;
    }

    public static boolean areEqual( Parent parent1, Parent parent2 )
    {
        if ( eitherIsNull( parent1, parent2 ) )
            return bothAreNull( parent1, parent2 );
        else if ( !areEqual( parent1.getArtifactId(), parent2.getArtifactId() ) )
            return false;
        else if ( !areEqual( parent1.getGroupId(), parent2.getGroupId() ) )
            return false;
        else if ( !areEqual( parent1.getId(), parent2.getId() ) )
            return false;
        else if ( !areEqual( parent1.getRelativePath(), parent2.getRelativePath() ) )
            return false;
        else if ( !areEqual( parent1.getVersion(), parent2.getVersion() ) )
            return false;
        else
            return true;
    }

    private static boolean areEqual( PluginManagement pluginManagement1, PluginManagement pluginManagement2 )
    {
        if ( eitherIsNull( pluginManagement1, pluginManagement2 ) )
            return bothAreNull( pluginManagement1, pluginManagement2 );
        else if ( !arePluginsEqual( pluginManagement1.getPlugins(), pluginManagement2.getPlugins() ) )
            return false;
        else
            return true;
    }

    public static boolean areEqual( Prerequisites prerequisites1, Prerequisites prerequisites2 )
    {
        if ( eitherIsNull( prerequisites1, prerequisites2 ) )
            return bothAreNull( prerequisites1, prerequisites2 );
        else if ( !areEqual( prerequisites1.getMaven(), prerequisites2.getMaven() ) )
            return false;
        else
            return true;
    }

    public static boolean areEqual( Properties properties1, Properties properties2 )
    {
        if ( eitherIsNull( properties1, properties2 ) )
            return bothAreNull( properties1, properties2 );
        else if ( properties1.size() != properties2.size() )
            return false;

        Set<String> prop1Names = properties1.stringPropertyNames();
        Set<String> prop2Names = properties2.stringPropertyNames();
        for ( String currName : prop1Names )
        {
            if ( !prop2Names.contains( currName ) )
                return false;
            else if ( !properties1.get( currName ).equals( properties2.get( currName ) ) )
                return false;
        }

        return true;
    }

    private static boolean areEqual( Relocation relocation1, Relocation relocation2 )
    {
        if ( eitherIsNull( relocation1, relocation2 ) )
            return bothAreNull( relocation1, relocation2 );
        else if ( !areEqual( relocation1.getArtifactId(), relocation2.getArtifactId() ) )
            return false;
        else if ( !areEqual( relocation1.getGroupId(), relocation2.getGroupId() ) )
            return false;
        else if ( !areEqual( relocation1.getMessage(), relocation2.getMessage() ) )
            return false;
        else if ( !areEqual( relocation1.getVersion(), relocation2.getVersion() ) )
            return false;
        else
            return true;
    }

    public static boolean areEqual( Reporting reporting1, Reporting reporting2 )
    {
        if ( eitherIsNull( reporting1, reporting2 ) )
            return bothAreNull( reporting1, reporting2 );
        else if ( !areEqual( reporting1.getExcludeDefaults(), reporting2.getExcludeDefaults() ) )
            return false;
        else if ( !areEqual( reporting1.getOutputDirectory(), reporting2.getOutputDirectory() ) )
            return false;
        else if ( !areReportPluginsEqual( reporting1.getPlugins(), reporting2.getPlugins() ) )
            return false;
        else
            return true;
    }

    private static boolean areEqual( RepositoryPolicy releases1, RepositoryPolicy releases2 )
    {
        if ( eitherIsNull( releases1, releases2 ) )
            return bothAreNull( releases1, releases2 );
        else if ( !areEqual( releases1.getChecksumPolicy(), releases2.getChecksumPolicy() ) )
            return false;
        else if ( !areEqual( releases1.getEnabled(), releases2.getEnabled() ) )
            return false;
        else if ( !areEqual( releases1.getUpdatePolicy(), releases2.getUpdatePolicy() ) )
            return false;
        else
            return true;
    }

    public static boolean areEqual( Scm scm1, Scm scm2 )
    {
        if ( eitherIsNull( scm1, scm2 ) )
            return bothAreNull( scm1, scm2 );
        else if ( !areEqual( scm1.getConnection(), scm2.getConnection() ) )
            return false;
        else if ( !areEqual( scm1.getDeveloperConnection(), scm2.getDeveloperConnection() ) )
            return false;
        else if ( !areEqual( scm1.getTag(), scm2.getTag() ) )
            return false;
        else if ( !areEqual( scm1.getUrl(), scm2.getUrl() ) )
            return false;
        else
            return true;
    }

    private static boolean areEqual( Site site1, Site site2 )
    {
        if ( eitherIsNull( site1, site2 ) )
            return bothAreNull( site1, site2 );
        else if ( !areEqual( site1.getId(), site2.getId() ) )
            return false;
        else if ( !areEqual( site1.getName(), site2.getName() ) )
            return false;
        else if ( !areEqual( site1.getUrl(), site2.getUrl() ) )
            return false;
        else
            return true;
    }

    private static boolean areEqual( String string1, String string2 )
    {
        // If either is null, return true iff both are null
        // If neither is null, return true iff o1 and o2 are the same sequence of characters
        return eitherIsNull( string1, string2 ) ? bothAreNull( string1, string2 ) : string1.equals( string2 );
    }

    /**
     * Exclusion does not implement a true equality method, so we need to compare lists of that ourselves
     */
    private static boolean areExclusionsEqual( List<Exclusion> list1, List<Exclusion> list2 )
    {
        if ( eitherIsNull( list1, list2 ) )
            return bothAreNull( list1, list2 );
        else if ( list1.size() != list2.size() )
            return false;

        for ( int i = 0; i < list1.size(); i++ )
        {
            Exclusion e1 = list1.get( i );
            Exclusion e2 = list2.get( i );

            if ( !areEqual( e1.getArtifactId(), e2.getArtifactId() ) )
                return false;
            else if ( !areEqual( e1.getGroupId(), e2.getGroupId() ) )
                return false;
        }

        return true;
    }

    private static boolean areExecutionsEqual( List<PluginExecution> executions1, List<PluginExecution> executions2 )
    {
        if ( eitherIsNull( executions1, executions2 ) )
            return bothAreNull( executions1, executions2 );
        else if ( executions1.size() != executions2.size() )
            return false;

        for ( int i = 0; i < executions1.size(); i++ )
        {
            PluginExecution e1 = executions1.get( i );
            PluginExecution e2 = executions2.get( i );

            if ( !areEqual( e1.getId(), e2.getId() ) )
                return false;
            else if ( !areEqual( e1.getInherited(), e2.getInherited() ) )
                return false;
            else if ( !areEqual( e1.getPhase(), e2.getPhase() ) )
                return false;
            else if ( !areConfigurationEqual( e1.getConfiguration(), e2.getConfiguration() ) )
                return false;
            else if ( !areEqual( e1.getGoals(), e2.getGoals() ) )
                return false;
        }

        return true;
    }

    public static boolean areLicensesEqual( List<License> licenses1, List<License> licenses2 )
    {
        if ( eitherIsNull( licenses1, licenses2 ) )
            return bothAreNull( licenses1, licenses2 );
        else if ( licenses1.size() != licenses2.size() )
            return false;

        for ( int i = 0; i < licenses1.size(); i++ )
        {
            License e1 = licenses1.get( i );
            License e2 = licenses2.get( i );

            if ( !areEqual( e1.getComments(), e2.getComments() ) )
                return false;
            else if ( !areEqual( e1.getDistribution(), e2.getDistribution() ) )
                return false;
            else if ( !areEqual( e1.getName(), e2.getName() ) )
                return false;
            else if ( !areEqual( e1.getUrl(), e2.getUrl() ) )
                return false;
        }

        return true;
    }

    public static boolean areMailingListsEqual( List<MailingList> mailingLists1, List<MailingList> mailingLists2 )
    {
        if ( eitherIsNull( mailingLists1, mailingLists2 ) )
            return bothAreNull( mailingLists1, mailingLists2 );
        else if ( mailingLists1.size() != mailingLists2.size() )
            return false;

        for ( int i = 0; i < mailingLists1.size(); i++ )
        {
            MailingList e1 = mailingLists1.get( i );
            MailingList e2 = mailingLists2.get( i );

            if ( !areEqual( e1.getArchive(), e2.getArchive() ) )
                return false;
            else if ( !areEqual( e1.getName(), e2.getName() ) )
                return false;
            else if ( !areEqual( e1.getOtherArchives(), e2.getOtherArchives() ) )
                return false;
            else if ( !areEqual( e1.getPost(), e2.getPost() ) )
                return false;
            else if ( !areEqual( e1.getSubscribe(), e2.getSubscribe() ) )
                return false;
            else if ( !areEqual( e1.getUnsubscribe(), e2.getUnsubscribe() ) )
                return false;
        }

        return true;
    }

    private static boolean areNotifiersEqual( List<Notifier> notifiers1, List<Notifier> notifiers2 )
    {
        if ( eitherIsNull( notifiers1, notifiers2 ) )
            return bothAreNull( notifiers1, notifiers2 );
        else if ( notifiers1.size() != notifiers2.size() )
            return false;

        for ( int i = 0; i < notifiers1.size(); i++ )
        {
            Notifier e1 = notifiers1.get( i );
            Notifier e2 = notifiers2.get( i );

            if ( !areEqual( e1.getAddress(), e2.getAddress() ) )
                return false;
            else if ( !areEqual( e1.getConfiguration(), e2.getConfiguration() ) )
                return false;
            else if ( !areEqual( e1.getType(), e2.getType() ) )
                return false;
        }

        return true;
    }

    private static boolean arePluginsEqual( List<Plugin> plugins1, List<Plugin> plugins2 )
    {
        if ( eitherIsNull( plugins1, plugins2 ) )
            return bothAreNull( plugins1, plugins2 );
        else if ( plugins1.size() != plugins2.size() )
            return false;

        for ( int i = 0; i < plugins1.size(); i++ )
        {
            Plugin e1 = plugins1.get( i );
            Plugin e2 = plugins2.get( i );

            if ( !areEqual( e1.getArtifactId(), e2.getArtifactId() ) )
                return false;
            else if ( !areDependenciesEqual( e1.getDependencies(), e2.getDependencies() ) )
                return false;
            else if ( !areExecutionsEqual( e1.getExecutions(), e2.getExecutions() ) )
                return false;
            else if ( !areEqual( e1.getExtensions(), e2.getExtensions() ) )
                return false;
            else if ( !areEqual( e1.getGroupId(), e2.getGroupId() ) )
                return false;
            else if ( !areEqual( e1.getId(), e2.getId() ) )
                return false;
            else if ( !areEqual( e1.getInherited(), e2.getInherited() ) )
                return false;
            else if ( !areEqual( e1.getKey(), e2.getKey() ) )
                return false;
            else if ( !areEqual( e1.getVersion(), e2.getVersion() ) )
                return false;
            else if ( !areConfigurationEqual( e1.getConfiguration(), e2.getConfiguration() ) )
                return false;
        }

        return true;
    }

    public static boolean areProfilesEqual( List<Profile> list1, List<Profile> list2 )
    {
        if ( eitherIsNull( list1, list2 ) )
            return bothAreNull( list1, list2 );
        else if ( list1.size() != list2.size() )
            return false;

        for ( int i = 0; i < list1.size(); i++ )
        {
            Profile e1 = list1.get( i );
            Profile e2 = list2.get( i );

            if ( !areDependenciesEqual( e1.getDependencies(), e2.getDependencies() ) )
                return false;
            else if ( !areEqual( e1.getDependencyManagement(), e2.getDependencyManagement() ) )
                return false;
            else if ( !areEqual( e1.getDistributionManagement(), e2.getDistributionManagement() ) )
                return false;
            else if ( !areEqual( e1.getId(), e2.getId() ) )
                return false;
            else if ( !areEqual( e1.getModules(), e2.getModules() ) )
                return false;
            else if ( !areRepositoriesEqual( e1.getPluginRepositories(), e2.getPluginRepositories() ) )
                return false;
            else if ( !areEqual( e1.getProperties(), e2.getProperties() ) )
                return false;
            else if ( !areEqual( e1.getReporting(), e2.getReporting() ) )
                return false;
            else if ( !areRepositoriesEqual( e1.getRepositories(), e2.getRepositories() ) )
                return false;
            else if ( !areEqual( e1.getSource(), e2.getSource() ) )
                return false;
            else if ( !areEqual( e1.getActivation(), e2.getActivation() ) )
                return false;
            else if ( !areEqual( e1.getBuild(), e2.getBuild() ) )
                return false;
        }

        return true;
    }

    private static boolean areReportPluginsEqual( List<ReportPlugin> plugins1, List<ReportPlugin> plugins2 )
    {
        if ( eitherIsNull( plugins1, plugins2 ) )
            return bothAreNull( plugins1, plugins2 );
        else if ( plugins1.size() != plugins2.size() )
            return false;

        for ( int i = 0; i < plugins1.size(); i++ )
        {
            ReportPlugin e1 = plugins1.get( i );
            ReportPlugin e2 = plugins2.get( i );

            if ( !areEqual( e1.getArtifactId(), e2.getArtifactId() ) )
                return false;
            else if ( !areEqual( e1.getGroupId(), e2.getGroupId() ) )
                return false;
            else if ( !areEqual( e1.getInherited(), e2.getInherited() ) )
                return false;
            else if ( !areEqual( e1.getKey(), e2.getKey() ) )
                return false;
            else if ( !areReportSetsEqual( e1.getReportSets(), e2.getReportSets() ) )
                return false;
            else if ( !areEqual( e1.getVersion(), e2.getVersion() ) )
                return false;
            else if ( !areConfigurationEqual( e1.getConfiguration(), e2.getConfiguration() ) )
                return false;
        }

        return true;
    }

    private static boolean areReportSetsEqual( List<ReportSet> reportSets1, List<ReportSet> reportSets2 )
    {
        if ( eitherIsNull( reportSets1, reportSets2 ) )
            return bothAreNull( reportSets1, reportSets2 );
        else if ( reportSets1.size() != reportSets2.size() )
            return false;

        for ( int i = 0; i < reportSets1.size(); i++ )
        {
            ReportSet e1 = reportSets1.get( i );
            ReportSet e2 = reportSets2.get( i );

            if ( !areEqual( e1.getId(), e2.getId() ) )
                return false;
            else if ( !areEqual( e1.getInherited(), e2.getInherited() ) )
                return false;
            else if ( !areEqual( e1.getReports(), e2.getReports() ) )
                return false;
            else if ( !areConfigurationEqual( e1.getConfiguration(), e2.getConfiguration() ) )
                return false;
        }

        return true;
    }

    public static boolean areRepositoriesEqual( List<Repository> pluginRepositories1,
                                                List<Repository> pluginRepositories2 )
    {
        if ( eitherIsNull( pluginRepositories1, pluginRepositories2 ) )
            return bothAreNull( pluginRepositories1, pluginRepositories2 );
        else if ( pluginRepositories1.size() != pluginRepositories2.size() )
            return false;

        for ( int i = 0; i < pluginRepositories1.size(); i++ )
        {
            Repository e1 = pluginRepositories1.get( i );
            Repository e2 = pluginRepositories2.get( i );

            if ( !areEqual( e1.getId(), e2.getId() ) )
                return false;
            else if ( !areEqual( e1.getLayout(), e2.getLayout() ) )
                return false;
            else if ( !areEqual( e1.getName(), e2.getName() ) )
                return false;
            else if ( !areRepositoryPolicyEqual( e1.getReleases(), e2.getReleases() ) )
                return false;
            else if ( !areRepositoryPolicyEqual( e1.getSnapshots(), e2.getSnapshots() ) )
                return false;
            else if ( !areEqual( e1.getUrl(), e2.getUrl() ) )
                return false;
        }

        return true;
    }

    private static boolean areRepositoryPolicyEqual( RepositoryPolicy releases1, RepositoryPolicy releases2 )
    {
        if ( eitherIsNull( releases1, releases2 ) )
            return bothAreNull( releases1, releases2 );
        else if ( !areEqual( releases1.getChecksumPolicy(), releases2.getChecksumPolicy() ) )
            return false;
        else if ( !areEqual( releases1.getEnabled(), releases2.getEnabled() ) )
            return false;
        else if ( !areEqual( releases1.getUpdatePolicy(), releases2.getUpdatePolicy() ) )
            return false;
        else
            return true;
    }

    /**
     * Resource does not implement a true equality method, so we need to compare lists of that ourselves
     */
    private static boolean areResourcesEqual( List<Resource> list1, List<Resource> list2 )
    {
        if ( eitherIsNull( list1, list2 ) )
            return bothAreNull( list1, list2 );
        else if ( list1.size() != list2.size() )
            return false;

        for ( int i = 0; i < list1.size(); i++ )
        {
            Resource e1 = list1.get( i );
            Resource e2 = list2.get( i );

            if ( !areEqual( e1.getDirectory(), e2.getDirectory() ) )
                return false;
            else if ( !areEqual( e1.getExcludes(), e2.getExcludes() ) )
                return false;
            else if ( !areEqual( e1.getFiltering(), e2.getFiltering() ) )
                return false;
            else if ( !areEqual( e1.getIncludes(), e2.getIncludes() ) )
                return false;
            else if ( !areEqual( e1.getMergeId(), e2.getMergeId() ) )
                return false;
            else if ( !areEqual( e1.getTargetPath(), e2.getTargetPath() ) )
                return false;
        }

        return true;
    }

    /**
     * Check if both objects are null (logical and)
     * 
     * @param o1 First Object
     * @param o2 Second Object
     * @return true iff First Object and Second Object is null
     */
    private static boolean bothAreNull( Object o1, Object o2 )
    {
        return o1 == null && o2 == null;
    }

    /**
     * Check if either object is null (logical inclusive or)
     * 
     * @param o1 First Object
     * @param o2 Second Object
     * @return true iff First Object or Second Object is null
     */
    private static boolean eitherIsNull( Object o1, Object o2 )
    {
        return o1 == null || o2 == null;
    }

    /**
     * Record why the equality check will return false
     */
    private static void recordFailReason( String reasonID )
    {
        if ( DEBUG > 0 )
        {
            System.out.println( "Equality check returning false, reason: '" + reasonID + "'" );
        }
    }
}
