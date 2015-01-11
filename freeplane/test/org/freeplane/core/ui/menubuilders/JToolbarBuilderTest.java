package org.freeplane.core.ui.menubuilders;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;

import org.freeplane.core.ui.components.FreeplaneToolBar;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class JToolbarBuilderTest {


	@Test
	public void createsEmptyToolbarComponent() {
		Entry toolbarEntry = new Entry();
		toolbarEntry.setBuilders(asList("toolbar"));
		
		final JToolbarBuilder toolbarBuilder = new JToolbarBuilder();
		toolbarBuilder.build(toolbarEntry);

		assertThat(toolbarEntry.getComponent().getClass(), CoreMatchers.<Object>is(FreeplaneToolBar.class));
	}
}