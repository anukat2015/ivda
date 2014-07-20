using Gratex.PerConIK.AstRcs.Svc.Interfaces;
using Gratex.PerConIK.AstRcs.Svc.Proxy;
using Gratex.PerConIK.Core.Svc;
using Gratex.PerConIK.UserActivity.Svc;
using Gratex.PerConIK.UserActivity.Svc.Interfaces;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using MyProject;

namespace Gratex.PerConIK.Gossip.AddIns.MyPlugin
{
    class ServiceClient
    {
        private AstRcsWcfSvcClient mAstRcsClient;
        private ActivitySvcClient mActivitySvcClient;
        public List<BranchDto> Branches { get; set; }

        private void connectServiceAST()
        {
            WcfClientSettings settings = new WcfClientSettings()
            {
                BaseAddress = "http://perconik.fiit.stuba.sk/AstRcs/",
                BindingType = WcfClientSettings.WcfBindingType.BasicHttp,
                BypassProxy = true,
                UserName = @"steltecia\PublicServices",
                Password = "FiitSvc123.",
                SecurityMode = new WcfSecurityMode(WcfSecurityMode.ModeType.NtlmTransportCredentialOnly)
            };
            mAstRcsClient = new AstRcsWcfSvcClient(settings);
            /*
            Branches = mAstRcsClient.LoadAllPages((client, pageIndex) => client.SearchBranches(new SearchBranchesRequest() { PageIndex = pageIndex }),
                                      response => response.Branches);
             * */
        }


        private void connectServiceActivity()
        {
            WcfClientSettings settings = new WcfClientSettings()
            {
                // http://perconik.fiit.stuba.sk/UserActivity.Old
                BaseAddress = "http://perconik.fiit.stuba.sk/useractivity",
                BindingType = WcfClientSettings.WcfBindingType.BasicHttp,
                BypassProxy = true,
                UserName = @"steltecia\PublicServices",
                Password = "FiitSvc123.",
                SecurityMode = new WcfSecurityMode(WcfSecurityMode.ModeType.NtlmTransportCredentialOnly)
            };

            mActivitySvcClient = new ActivitySvcClient(settings);
        }

        private void measureRatio()
        {

            SearchUsersResponse response = mAstRcsClient.SearchUsers(new SearchUsersRequest() { Login = "a" });
            List<UserDto> users = response.Users;
            UserDto vyvojar = users.ElementAt(0);

            ActivityFilter filter = new ActivityFilter()
            {
                StartTimeFrom = new DateTime(2013, 2, 18, 7, 33, 9),
                EndTimeTo = new DateTime(2013, 2, 18, 8, 33, 9),
                PageSize = 10,
                User = vyvojar.Login
            };

            // Ako často je potom tento kód prepisovaný je v ASTR
            ActivityDto[] activities = null;
            while ((activities = mActivitySvcClient.GetActivities(filter)).Length > 0) // prechadzaj aktivity pouzivatela
            {
                foreach (var activity in activities)
                {
                    foreach (var activityEvent in activity.Events) // prechadzaj vsetky udalosti pre aktivitu
                    {
                        // IdeSlnPrjEventDto
                        if (activityEvent is IdeCodeOperationDto) // ide o operaciu nad kodom
                        {
                            IdeCodeOperationDto codeEvent = activityEvent as IdeCodeOperationDto;
                            if (codeEvent.OperationType == IdeCodeOperationTypeEnum.PasteFromWeb)
                            {
                                // codeEvent.ProjectName ma rovnake project name teda v ramci projektu
                                // Represent logical project as metadata about set of source code files, which produce some output.


                                
                                // codeEvent.WebUrl je adresa
                                // codeEvent.Code je kod co bo lskopirovany
                                IdeDocumentDto document = codeEvent.Document; // toto by mal byt dokument kde bolo nieco skopirovane
                                
                                if(document != null) { // opracia by mohla byt nad ziadnym dokumentom
                                    // document.Path cesta k suboru
                                    // document.BranchName branch cize konar kde su spolocne vetvy, to testovat nemusim
                                    // document.ChangesetIdInRcs changeset ID unique within AST RCS system, in which the entity version has been created
                                    // document.RcsServer

                                     // Prechadzam vsetky checkiny nad celym dokumentom alebo nad tou oblastou?
                                     
                                    // Ak idem na oblast ... tak dana cast kodu bola upravena, prepisana a vznikla tak udalost. Nasledne dany subor bol komitnuty.
                                    // AK mam pevne hranicie [x,y] ktorymi som definoval oblast. TAk pri pisani sa tieto hranice posuaju a uz neplatia.
                                    // Musel by som vypocitat novu hranicu. A hladat neskor aj pre nu.
                                    // To je otazka skor na repozitar ci on nevie zistit, kolko krat bol dany kod upravovany
                                    IdeCheckinDto svn;

                                    // repozitar funguje najprv lokalne "checkin" a potom sa posiela "committed" cely na server
                                    // teraz je otazka co chceme sledovat, povodny kod?
                                    // alebo chceme sledovat kod ktory sa uz upravil a poslal na server?

                                    /*
                                    Vďaka takejto vizualizácii, by sme dokázali odpovedať na viacero zaujímavých otázok pri vývoji softvérového produktu. 
                                Napríklad: "Ak vývojár často používa prehliadač (resp. portál) pri písaní svojho kódu, ako často je potom tento kód prepisovaný? 
                                    Platí táto tendencia na všetky jeho kódy? Platí to u každého iného vývojára rovnako?" 

                                     Kedsi uvedomim, ze to ma len poukazat na pomer ..... je to potom odhad
                                    
                                        ALE AKO KURVA ZISTIM, ZE KOLKO KRAT BOL SUBOR PREPISANY ... ci lokalne ci na urovni repozitara, ci ako dlho bol prepisovany (konstrukciou udalosti)
                                        a potom na urovni entit?
                                     
                                     zalezi od Rrevision control system RCS ... oni pouzivaju TFS , teda treba hladat TFS api
                                     teamfoundation                                  
                                     Gets the file diff in unix format.
                                     Detects if code element has been changed. Code element has been changed, if intersection of element line range and change line range is not empty set.
                                     
                                     * 
                                     DOLEZITA JE KONECNOST
                                     tzv uzivatel skopiroval kod, prepisal ho ....... potom ale mohol skopirovat kod znovu a prepisat ho a to je ako keby zmena nad novym skopirovanim
                                     cize prve hladanie moze ist az po udalost kedy bolo opat pouzite kopirovanie nad danou entitnou
                                    */
                                    /*
                                    public RcsChangeset GetChangeset(string changesetId, bool includeChanges, Func<string, bool> changePathFilter)
                                    C:\Users\Seky\Documents\Visual Studio 2012\Projects\PerConIK.Core-Trunk-Package\PerConIK\Core\Main\AstEngine\EntityEngine.cs
                                    C:\Users\Seky\Documents\Visual Studio 2012\Projects\PerConIK.Core-Trunk-Package\PerConIK\Core\Main\EntityVersionLinker\EntityStructures.cs
                                     public static void ExtractScFileCodeElementChanges(VersionControlServer vcs, RCSServer rcsServer, File file)
                                    */
                                    // Hodnota atribútu IdInRcs tejto udalosti predstavuje changeset id. Hodnota atribútu RcsServer tejto udalosti predstavuje urovni servera. 
                                }
                                // kod je ohraniceny StartRowIndex, StartColumnIndex do EndRowIndex, EndColumnIndex 
                                // start row of the entity version within source code file v AKTUALNOM subore
                            }
                        }
                    }
                }

                filter.PageStartIndex += activities.Length;
            }
            C:\Users\Seky\Documents\Visual Studio 2012\Projects\PerConIK.Core-Trunk-Package\PerConIK\Core\Main\Authors\Metrics\LineMatrics.cs
            // Ak vývojár často používa prehliadač (resp. portál) pri písaní svojho kódu, ako často je potom tento kód prepisovaný? 
            // - Pozerat sa na ako casto pouziva prehliadac ?
            // Platí táto tendencia na všetky jeho kódy? ... filter podla suborov, respektive pohlad len na subory
            // Platí to u každého iného vývojára rovnako?" ... vyberat podla vyvojara
            // + zamerat sa na filtre podla obdobia aktivit

            // Granularita na zaklade casu ... cize pozriem na jednu hodinu, co napisal za hodinu a co bolo v tomto kode prepisane ... blbost

            // !!!! musim zistit vztah ze tento kod bol skopirovany a potom prepisany !!!!
            // filter: bol prepisany nim alebo bol prepisany niekym inym?

            // pohlad podla usera, stanice, dna, projektu
            // co bolo vakych suboroch skopirovane
            // co sa kopirovalo najcastejsie

            // Mnohé vizualizačné techniky neponúkajú možnosť zobraziť vývoj softvérového produktu z pohľadu jednotlivých krokov vývojára. splnene
            // Ak vývojár často používa prehliadač (resp. portál) pri písaní svojho kódu, ako často je potom tento kód prepisovaný? Platí táto tendencia na všetky jeho kódy?
            // to ma timeline ukazat ze jeho kody boli alebo casto prepisovane

        }


        static void Main()
        {
            ServiceClient c = new ServiceClient();
            c.connectServiceActivity();
            c.connectServiceAST();
            c.measureRatio();
        }
    }
}
