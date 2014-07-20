using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace MyProject
{
//using System;
//using System.Collections.Generic;
//using System.Linq;
//using System.Text;
//using Gratex.PerConIK.Core.Parsers;
//using Microsoft.TeamFoundation.VersionControl.Client;
//using Microsoft.TeamFoundation.Client;
//using System.Collections;
//using System.IO;
//using Microsoft.TeamFoundation.VersionControl.Common;
//using System.Text.RegularExpressions;
//using ICSharpCode.NRefactory.CSharp;
//using Gratex.PerConIK.Authors;

//namespace Gratex.PerConIK.Authors.Sample
//{
//    class Program
//    {
//        static Regex changeFilter = new Regex(@"^(?<BeforeStart>\d+)(,(?<BeforeEnd>\d+))?(?<ChangeType>[a-z])(?<AfterStart>\d+)(,(?<AfterEnd>\d+))?", RegexOptions.IgnoreCase | RegexOptions.Multiline);

//        static void Main(string[] args)
//        {
//            //Solution.ParseAuthorsInSolution(@"C:\Work\PerConIK\MiniProjects\Gratex.PerConIK.MiniProjects-Trunk\PerConIK\MiniProjects\OneNote\OneNote.sln");
//        }
//    }
//}

using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Microsoft.TeamFoundation.VersionControl.Client;
using Microsoft.TeamFoundation.Client;
using System.Collections;
using System.IO;
using Microsoft.TeamFoundation.VersionControl.Common;
using System.Text.RegularExpressions;
using ICSharpCode.NRefactory.CSharp;
using Gratex.PerConIK.Authors;
using Gratex.PerConIK.Core.Parsers.VisualStudio;
using System.Xml.Serialization;

namespace Gratex.PerConIK.Authors.Sample
{
    class Program
    {
        static void Main(string[] args)
        {
            //string slnFilePath = @"C:\WORK\PerConIK\Core\PerConIK.Core-Trunk\PerConIK\Core\Main\PerConIK.sln";
            string slnFilePath = @"C:\WORK\PerConIK\Core\PerConIK.Core-Trunk\PerConIK\Core\Sandbox\AuthorsTest\AuthorsTest.sln";

            RCSServer rcsServer = Solution.SourceCodeChangesInSolutionByLastVersion(slnFilePath);
            Dictionary<string, Metrics.Project> projects = Metrics.LineMatrics.EvaluateMatrics(rcsServer);


            Console.WriteLine("Top three authors");
            Console.WriteLine("*****************");
            foreach (Metrics.Project project in projects.Values)
            {
                Console.WriteLine(project.GUID + ":" + project.Path);
                foreach (Metrics.Element element in project.Elements.Values)
                {
                    Console.WriteLine("   " + element.Type.ToString() + ":" + element.Path);
                    Console.WriteLine("      Weighted Modification Rate");
                    foreach (Metrics.Author author in element.TopThreeAuthorsWeightedModificationRate())
                    {
                        Console.WriteLine("         " + author.Login + " : " + author.CalculateWeightedModificationRate());
                    }
                    Console.WriteLine("      Weighted Modification Ration");
                    foreach (Metrics.AuthorModifications authorMods in element.TopThreeAuthorsWeightedModificationRatio())
                    {
                        Console.WriteLine("         " + authorMods.Author.Login + " : " + authorMods.WeightedRatio);
                    }
                }
            }

            Console.ReadKey();
        }
    }

}
