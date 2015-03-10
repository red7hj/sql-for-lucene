/*
 * *
 *
 *
 * Copyright 2015 Bill Bejeck
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */

package bbejeck.nosql.antlr;

import bbejeck.nosql.antlr.generated.LuceneSqlLexer;
import bbejeck.nosql.antlr.generated.LuceneSqlParser;
import bbejeck.nosql.lucene.LuceneQueryListener;
import bbejeck.nosql.lucene.QueryParseResults;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * User: Bill Bejeck
 * Date: 10/14/14
 * Time: 10:10 PM
 */
public class AntlrLuceneFunctions {

    private static Function<String, ANTLRInputStream> createAntlrInputStream = ANTLRInputStream::new;
    private static Function<ANTLRInputStream, LuceneSqlLexer> createLexer = LuceneSqlLexer::new;
    private static Function<LuceneSqlLexer, CommonTokenStream> createTokenStream = CommonTokenStream::new;
    private static Function<CommonTokenStream, LuceneSqlParser> createLuceneSqlParser = LuceneSqlParser::new;
    private static Function<LuceneSqlParser, LuceneSqlParser.QueryContext> createParseTree = LuceneSqlParser::query;
    private static Supplier<ParseTreeWalker> parseTreeWalkerSupplier = ParseTreeWalker::new;
    private static Supplier<LuceneQueryListener> luceneQueryListenerSupplier = LuceneQueryListener::new;

    private static Function<String, LuceneSqlParser> createParser = createAntlrInputStream.andThen(createLexer).andThen(createTokenStream).andThen(createLuceneSqlParser);

    private static Function<LuceneSqlParser, QueryParseResults> parse = parser -> {
        LuceneQueryListener listener = luceneQueryListenerSupplier.get();
        ParseTree parseTree = createParseTree.apply(parser);
        ParseTreeWalker walker = parseTreeWalkerSupplier.get();
        walker.walk(listener, parseTree);
        return listener.getParseResults();
    };


    public static QueryParseResults parseQuery(String query) {
        return parse.apply(createParser.apply(query));
    }
}