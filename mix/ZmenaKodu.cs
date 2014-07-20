using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MyProject
{
    class ZmenaKodu
    {
        //    // faza 1: ziskanie mnoziny suborov (treba nahradit z vyssie zakomentovaneho)
        //    TfsTeamProjectCollection tfs = new TfsTeamProjectCollection(new Uri("http://psi:8080"));
        //    tfs.EnsureAuthenticated();
        //    VersionControlServer vcs = tfs.GetService<VersionControlServer>();
        //    // RCS a Project pojde z parametra funkcie, pricom sa vytvoria na vyssej urovni (mimo tejto funkcie)
        //    RCSServer rcsServer = new RCSServer("http://psi:8080");
        //    Project project = new Project();
        //    // File treba vytvorit pre kazdy subor
        //    File file = new File(project);
        //    file.Path = @"$\PerConIK\Core\Trunk\Sandbox\AuthorsTest\AuthorsTest\Program.cs"; // ziska sa z parametra funkcie

        //    // faza 2.1: getnutie historie
        //    //ChangesetVersionSpec startVersion = new ChangesetVersionSpec(708486); // pociatocna verzia,
        //    var historyTmp = vcs.QueryHistory(@"$\PerConIK\Core\Trunk\Sandbox\AuthorsTest\AuthorsTest\Program.cs",
        //      VersionSpec.Latest,
        //      0,
        //      RecursionType.None,
        //      "",
        //      null, //from begining. dat ChangesetVersionSpec pre query od nejakej verzie
        //      VersionSpec.Latest,
        //      Int32.MaxValue,
        //      true,
        //      false, true, false);

        //    // faza 2.2: zoradenie
        //    IEnumerable<Changeset> history = historyTmp.OfType<Changeset>().OrderBy(a => a.ChangesetId);

        //    // faza 3: reprezentacia elementov kodu, zmien a ich namapovanie; vytvorenie zmien elementov kodov 
        //    Changeset originalChangeset = null;
        //    ExtractContent originalData = null;
        //    ExtractContent newData = null;
        //    foreach (Changeset newChangeset in history)
        //    {
        //        FileChangeType fileChangeType = Solution.GetFileChangeType(newChangeset);
        //        Console.WriteLine(fileChangeType);
        //        Console.WriteLine("***********");

        //        if (fileChangeType == FileChangeType.Edit || fileChangeType == FileChangeType.Add)
        //        {
        //            ChangeSet changeSet = Solution.GetChangeSetInfo(newChangeset, rcsServer); 
        //            FileChange fileChange = new FileChange(changeSet, file, fileChangeType);
        //            Stream scStream = Solution.GetFileStream(newChangeset);
        //            newData = Solution.ScFileToAstElements(scStream);

        //            if (originalChangeset != null && fileChangeType == FileChangeType.Edit)
        //            {
        //                Console.WriteLine("------------------------------------");
        //                Console.WriteLine(string.Format("    Comparing changesets [{0}] and [{1}]...", originalChangeset.ChangesetId, newChangeset.ChangesetId));
        //                Console.WriteLine(string.Format("    Creation dates [{0}] and [{1}]", originalChangeset.CreationDate, newChangeset.CreationDate));
        //                Console.WriteLine(string.Format("    Committers [{0}] and [{1}]", originalChangeset.Committer, newChangeset.Committer));
        //                // tu treba riesit, ak je stara alebo nova verzia suboru prazdna, potom je to bud vsetko add alebo delete
        //                string diff = Solution.GetScFileDiff(vcs, newChangeset, originalChangeset);
        //                Console.WriteLine(diff);
        //                List<CodeChange> codeChanges = Solution.ParseCodeChanges(diff);
        //                Solution.MapCodeElementChanges(fileChange, originalData.ExtractedCodeElements, newData.ExtractedCodeElements, codeChanges);
        //                Console.WriteLine("    ------- ");
        //                Console.ReadKey();
        //            }
        //            else if (fileChangeType == FileChangeType.Add)
        //            {
        //                Solution.MapCodeElementChanges(fileChange, newData.ExtractedCodeElements);
        //            }
        //            originalChangeset = newChangeset;
        //            originalData = newData;
        //        }
        //    }
        //    Console.WriteLine("=====end======");
        //    //List<Authorship> authorshipList = CalculateCodeElementAuthors(codeElementChanges);
        //    //foreach (Authorship authorship in authorshipList)
        //    //{
        //    //    Console.WriteLine("{0},{1},{2}", authorship.FullName, authorship.Author, authorship.ChangeCount);
        //    //}
        //    Console.ReadKey();
        //}

        //private static void AddCodeElementChange(Dictionary<string, List<CodeElementChange>> codeElementChanges, string fullName, CodeElementChange codeElementChange)
        //{
        //    List<CodeElementChange> singleCodeElementChanges;
        //    if (codeElementChanges.TryGetValue(fullName, out singleCodeElementChanges) == false) // neexistuje element s danym fullName
        //    {
        //        singleCodeElementChanges = new List<CodeElementChange>();
        //        codeElementChanges.Add(fullName, singleCodeElementChanges);
        //    }
        //    singleCodeElementChanges.Add(codeElementChange); // ci existuje, alebo som vytvoril novy zoznam pre dany element, pridam tam jeho zmenu
        //}


        //private static List<Authorship> CalculateCodeElementAuthors(Dictionary<string, List<CodeElementChange>> codeElementChanges)
        //{
        //    List<Authorship> authorshipList = new List<Authorship>();
        //    foreach (var element in codeElementChanges)
        //    {
        //        Dictionary<string, Authorship> elementAuthorship = new Dictionary<string, Authorship>();
        //        foreach (CodeElementChange change in element.Value)
        //        {
        //            // zatial ratam iba metody
        //            if (change.LocationAfter.Type == CodeElementType.Method)
        //            {
        //                Authorship authorship;
        //                if (elementAuthorship.TryGetValue(change.Author, out authorship) == false) // neexistuje dany autor
        //                {
        //                    authorship = new Authorship();
        //                    authorship.Author = change.Author;
        //                    authorship.FullName = change.LocationAfter.FullName;
        //                    elementAuthorship.Add(change.Author, authorship);
        //                }
        //                int changeCount = (change.LocationAfter.EndLine - change.LocationAfter.StartLine) + 1;
        //                authorship.AddCount(changeCount);
        //            }
        //        }
        //        authorshipList.AddRange(elementAuthorship.Values);
        //    }

        //    return authorshipList;
        //}
    }
}
