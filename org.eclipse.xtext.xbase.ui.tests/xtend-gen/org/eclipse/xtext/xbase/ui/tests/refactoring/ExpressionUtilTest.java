package org.eclipse.xtext.xbase.ui.tests.refactoring;

import com.google.inject.Inject;
import java.util.List;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.ITextRegion;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.lib.Exceptions;
import org.eclipse.xtext.xbase.lib.Functions.Function1;
import org.eclipse.xtext.xbase.lib.Functions.Function2;
import org.eclipse.xtext.xbase.lib.IterableExtensions;
import org.eclipse.xtext.xbase.lib.ListExtensions;
import org.eclipse.xtext.xbase.ui.refactoring.ExpressionUtil;
import org.eclipse.xtext.xbase.ui.tests.AbstractXbaseTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Jan Koehnlein
 */
@SuppressWarnings("all")
public class ExpressionUtilTest extends AbstractXbaseTestCase {
  @Inject
  private ExpressionUtil util;
  
  @Inject
  private ParseHelper<XExpression> parseHelper;
  
  @Inject
  private ValidationTestHelper validationHelper;
  
  @Inject
  private ILocationInFileProvider locationInFileProvider;
  
  @Test
  public void testSelectedExpression() {
    this.assertExpressionSelected("$$123+456", "123");
    this.assertExpressionSelected("$1$23+456", "123");
    this.assertExpressionSelected("$12$3+456", "123");
    this.assertExpressionSelected("$123$+456", "123");
    this.assertExpressionSelected("1$$23+456", "123");
    this.assertExpressionSelected("12$$3+456", "123");
    this.assertExpressionSelected("123$$+456", "123");
    this.assertExpressionSelected("123+$$456", "456");
    this.assertExpressionSelected("123$+$456", "123+456");
    this.assertExpressionSelected("12$3+$456", "123+456");
    this.assertExpressionSelected("123$+4$56", "123+456");
    this.assertExpressionSelected("if(Boolean.TRUE$$) null", "Boolean.TRUE");
    this.assertExpressionSelected("if(Boolean.TRUE)$$ null", "if(Boolean.TRUE) null");
    this.assertExpressionSelected("if(Boolean.TRUE) null$$ else null", "null");
    this.assertExpressionSelected("if(Boolean.TRUE) null $$else null", "if(Boolean.TRUE) null else null");
    this.assertExpressionSelected("newArrayList(\'jan\',\'hein\',\'claas\',\'pit\').map[$it|toFirstUpper]$", "[it|toFirstUpper]");
    this.assertExpressionSelected("newArrayList(\'jan\',\'hein\',\'claas\',\'pit\').map[it$|$toFirstUpper]", "[it|toFirstUpper]");
    this.assertExpressionSelected("newArrayList(\'jan\',\'hein\',\'claas\',\'pit\').map$[it|toFirstUpper]$", "[it|toFirstUpper]");
    this.assertExpressionSelected("newArrayList(\'jan\',\'hein\',\'claas\',\'pit\').map$[it|toFirstUpper$]", "[it|toFirstUpper]");
    this.assertExpressionSelected("newArrayList(\'jan\',\'hein\',\'claas\',\'pit\').map$[it|toFirstUpper$]", "[it|toFirstUpper]");
  }
  
  @Test
  public void testSelectedExpression_01() {
    this.assertExpressionSelected("newArrayList($$true)", "true");
    this.assertExpressionSelected("newArrayList($$\'ru\')", "\'ru\'");
    this.assertExpressionSelected("newArrayList($\'ru\'$)", "\'ru\'");
    this.assertExpressionSelected("newArrayList(\'ru\'$$)", "\'ru\'");
    this.assertExpressionSelected("newArrayList(\'ru$$\')", "\'ru\'");
    this.assertExpressionSelected("newArrayList(\'$ru$\')", "\'ru\'");
  }
  
  @Test
  public void testSelectedExpression_02() {
    this.assertExpressionSelected("if(Boolean.$$TRUE) null", "Boolean.TRUE");
  }
  
  @Test
  public void testSelectedExpression_03() {
    this.assertExpressionSelected("newArrayList($$#[42])", "#[42]");
  }
  
  @Test
  public void testSelectedExpression_04() {
    this.assertExpressionSelected("newArrayList($$#{42})", "#{42}");
  }
  
  @Test
  public void testSelectedExpression_05() {
    this.assertExpressionSelected("newArrayList($${42})", "{42}");
  }
  
  @Test
  public void testSelectedExpression_06() {
    this.assertExpressionSelected("newArrayList($ {$42})", "{42}");
  }
  
  @Test
  public void testBug401082() {
    this.assertExpressionSelected("{ var Object x val result = ($(x as String).toString$ ?:\"foo\") }", "(x as String).toString");
    this.assertExpressionSelected("{ var Object x val result = ($(x as String).$toString ?:\"foo\") }", "(x as String).toString");
    this.assertExpressionSelected("{ var Object x val result = ($(x as String)$.toString ?:\"foo\") }", "x as String");
  }
  
  @Test
  public void testSelectedExpressions() {
    this.assertSiblingExpressionsSelected("$$123+456", "123");
    this.assertSiblingExpressionsSelected("$1$23+456", "123");
    this.assertSiblingExpressionsSelected("$12$3+456", "123");
    this.assertSiblingExpressionsSelected("$123$+456", "123");
    this.assertSiblingExpressionsSelected("1$$23+456", "123");
    this.assertSiblingExpressionsSelected("12$$3+456", "123");
    this.assertSiblingExpressionsSelected("123$$+456", "123");
    this.assertSiblingExpressionsSelected("123+$$456", "456");
    this.assertSiblingExpressionsSelected("123$+$456", "123+456");
    this.assertSiblingExpressionsSelected("12$3+$456", "123+456");
    this.assertSiblingExpressionsSelected("123$+4$56", "123+456");
    this.assertSiblingExpressionsSelected("if(Boolean.$$TRUE) null", "Boolean.TRUE");
    this.assertSiblingExpressionsSelected("if(Boolean.TRUE$$) null", "Boolean.TRUE");
    this.assertSiblingExpressionsSelected("if(Boolean.TRUE)$$ null", "if(Boolean.TRUE) null");
    this.assertSiblingExpressionsSelected("if(Boolean.TRUE) $$null", "null");
    this.assertSiblingExpressionsSelected("if(Boolean.TRUE) null$$ else null", "null");
    this.assertSiblingExpressionsSelected("if(Boolean.TRUE) null $$else null", "if(Boolean.TRUE) null else null");
  }
  
  @Test
  public void testSelectedExpressions_1() {
    this.assertSiblingExpressionsSelected("{ val x=$1 val y=3$ val z=5 }", "val x=1 val y=3");
    this.assertSiblingExpressionsSelected("{ val x=1$ val y=3 $val z=5 }", "val y=3");
    this.assertSiblingExpressionsSelected("{ val x=1 $val y=3$ val z=5 }", "val y=3");
    this.assertSiblingExpressionsSelected("{ val x=1 $val y=3 $val z=5 }", "val y=3");
    this.assertSiblingExpressionsSelected("{ val x=1 $val y=3 v$al z=5 }", "val y=3 val z=5");
  }
  
  @Test
  public void testInsertionPoint() {
    this.assertInsertionPoint("{ val x = 1 $2+3 }", "2+3");
    this.assertInsertionPoint("{ val x = 1 2$+3 }", "2+3");
    this.assertInsertionPoint("{ val x = 1 2+$3 }", "2+3");
    this.assertInsertionPoint("{ val x = $1 2+$3 }", "val x = 1");
    this.assertInsertionPoint("{ val x = 1$ 2+$3 }", "val x = 1");
  }
  
  @Test
  public void testInsertionPointIf() {
    this.assertInsertionPoint("if($1==2.intValue) true", null);
    this.assertInsertionPoint("{ if($1==2.intValue) true }", "if(1==2.intValue) true");
    this.assertInsertionPoint("if(1==2.intValue) $true", "true");
    this.assertInsertionPoint("if(1==2.intValue) true else $false", "false");
    this.assertInsertionPoint("if(1==2.intValue) { val x = 7 + $8 }", "val x = 7 + 8");
  }
  
  @Test
  public void testInsertionPointSwitch() {
    this.assertInsertionPoint("switch 1 { case 1: 2+$3 }", "2+3");
    this.assertInsertionPoint("switch 1 { case 2: true default: 2+$3 }", "2+3");
  }
  
  @Test
  public void testInsertionPointWhile() {
    this.assertInsertionPoint("while(true) new $String()", "new String()");
    this.assertInsertionPoint("while($true) new $String()", null);
    this.assertInsertionPoint("do new $String() while(true)", "new String()");
    this.assertInsertionPoint("do new String() while($true)", null);
  }
  
  @Test
  public void testInsertionPointFor() {
    this.assertInsertionPoint("for(i: 1..2) new $String()", "new String()");
    this.assertInsertionPoint("for(i: $1..2) new $String()", null);
  }
  
  @Test
  public void testInsertionPointClosure() {
    this.assertInsertionPoint("[|2+$3]", "2+3");
    this.assertInsertionPoint("[|2$+3]", "2+3");
    this.assertInsertionPoint("[|$2+3]", "2+3");
  }
  
  @Test
  public void testInsertionPointTry() {
    this.assertInsertionPoint("try 2+$3 catch(Exception e) true", "2+3");
    this.assertInsertionPoint("try true catch(Exception e) new $String()", "new String()");
    this.assertInsertionPoint("try true finally new $String()", "new String()");
  }
  
  protected void assertExpressionSelected(final String modelWithSelectionMarkup, final String expectedSelection) {
    final String cleanedModel = modelWithSelectionMarkup.replaceAll("\\$", "");
    final XExpression expression = this.parse(cleanedModel);
    final int selectionOffset = modelWithSelectionMarkup.indexOf("$");
    int _lastIndexOf = modelWithSelectionMarkup.lastIndexOf("$");
    int _minus = (_lastIndexOf - selectionOffset);
    final int selectionLength = (_minus - 1);
    Resource _eResource = expression.eResource();
    TextSelection _textSelection = new TextSelection(selectionOffset, selectionLength);
    final XExpression selectedExpression = this.util.findSelectedExpression(((XtextResource) _eResource), _textSelection);
    final ITextRegion selectedRegion = this.locationInFileProvider.getFullTextRegion(selectedExpression);
    int _offset = selectedRegion.getOffset();
    int _offset_1 = selectedRegion.getOffset();
    int _length = selectedRegion.getLength();
    int _plus = (_offset_1 + _length);
    Assert.assertEquals(expectedSelection, cleanedModel.substring(_offset, _plus));
  }
  
  protected void assertSiblingExpressionsSelected(final String modelWithSelectionMarkup, final String expectedSelection) {
    final String cleanedModel = modelWithSelectionMarkup.replaceAll("\\$", "");
    final XExpression expression = this.parse(cleanedModel);
    final int selectionOffset = modelWithSelectionMarkup.indexOf("$");
    int _lastIndexOf = modelWithSelectionMarkup.lastIndexOf("$");
    int _minus = (_lastIndexOf - selectionOffset);
    final int selectionLength = (_minus - 1);
    Resource _eResource = expression.eResource();
    TextSelection _textSelection = new TextSelection(selectionOffset, selectionLength);
    final List<XExpression> selectedExpressions = this.util.findSelectedSiblingExpressions(((XtextResource) _eResource), _textSelection);
    final Function1<XExpression, ITextRegion> _function = (XExpression it) -> {
      return this.locationInFileProvider.getFullTextRegion(it);
    };
    final Function2<ITextRegion, ITextRegion, ITextRegion> _function_1 = (ITextRegion a, ITextRegion b) -> {
      return a.merge(b);
    };
    final ITextRegion selectedRegion = IterableExtensions.<ITextRegion>reduce(ListExtensions.<XExpression, ITextRegion>map(selectedExpressions, _function), _function_1);
    int _offset = selectedRegion.getOffset();
    int _offset_1 = selectedRegion.getOffset();
    int _length = selectedRegion.getLength();
    int _plus = (_offset_1 + _length);
    Assert.assertEquals(expectedSelection, cleanedModel.substring(_offset, _plus));
  }
  
  protected void assertInsertionPoint(final String modelWithInsertionMarkup, final String expectedSuccessor) {
    final String cleanedModel = modelWithInsertionMarkup.replaceAll("\\$", "");
    final XExpression expression = this.parse(cleanedModel);
    final int selectionOffset = modelWithInsertionMarkup.indexOf("$");
    Resource _eResource = expression.eResource();
    TextSelection _textSelection = new TextSelection(selectionOffset, 0);
    final XExpression selectedExpression = this.util.findSelectedExpression(((XtextResource) _eResource), _textSelection);
    final XExpression successor = this.util.findSuccessorExpressionForVariableDeclaration(selectedExpression);
    if ((expectedSuccessor == null)) {
      Assert.assertNull(successor);
    } else {
      Assert.assertNotNull(successor);
      final ITextRegion selectedRegion = this.locationInFileProvider.getFullTextRegion(successor);
      int _offset = selectedRegion.getOffset();
      int _offset_1 = selectedRegion.getOffset();
      int _length = selectedRegion.getLength();
      int _plus = (_offset_1 + _length);
      Assert.assertEquals(expectedSuccessor, cleanedModel.substring(_offset, _plus));
    }
  }
  
  protected XExpression parse(final CharSequence string) {
    try {
      XExpression _xblockexpression = null;
      {
        final XExpression expression = this.parseHelper.parse(string);
        this.validationHelper.assertNoErrors(expression);
        _xblockexpression = expression;
      }
      return _xblockexpression;
    } catch (Throwable _e) {
      throw Exceptions.sneakyThrow(_e);
    }
  }
}
