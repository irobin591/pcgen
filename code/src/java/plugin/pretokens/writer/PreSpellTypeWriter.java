/*
 * PrerequisiteSpellTypeWriter.java
 *
 * Copyright 2004 (C) Frugal <frugal@purplewombat.co.uk>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.       See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 *
 *
 *
 *
 */
package plugin.pretokens.writer;

import pcgen.core.prereq.Prerequisite;
import pcgen.core.prereq.PrerequisiteOperator;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.output.prereq.AbstractPrerequisiteWriter;
import pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface;

import java.io.IOException;
import java.io.Writer;

public class PreSpellTypeWriter extends AbstractPrerequisiteWriter implements
		PrerequisiteWriterInterface
{

	/**
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#kindHandled()
	 */
    @Override
	public String kindHandled()
	{
		return "spelltype";
	}

	/**
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#operatorsHandled()
	 */
    @Override
	public PrerequisiteOperator[] operatorsHandled()
	{
		return new PrerequisiteOperator[]{PrerequisiteOperator.GTEQ,
			PrerequisiteOperator.LT};
	}

	/**
	 * @see pcgen.persistence.lst.output.prereq.PrerequisiteWriterInterface#write(java.io.Writer, pcgen.core.prereq.Prerequisite)
	 */
    @Override
	public void write(Writer writer, Prerequisite prereq)
		throws PersistenceLayerException
	{
		checkValidOperator(prereq, operatorsHandled());

		try
		{
			if (prereq.getOperator().equals(PrerequisiteOperator.LT))
			{
				writer.write('!');
			}

			writer.write("PRE" + kindHandled().toUpperCase() + ':'
					+ (prereq.isOverrideQualify() ? "Q:" : "") + "1,");
			writer.write(prereq.getKey());
			writer.write('=');
			writer.write(prereq.getOperand());
		}
		catch (IOException e)
		{
			throw new PersistenceLayerException(e.getMessage());
		}
	}

	/**
	 * @see pcgen.persistence.lst.output.prereq.AbstractPrerequisiteWriter#specialCase(java.io.Writer writer, pcgen.core.prereq.Prerequisite prereq)
	 */
	@Override
	public boolean specialCase(Writer writer, Prerequisite prereq)
		throws IOException
	{
		PrerequisiteOperator po = getConsolidateMethod(kindHandled(), prereq, true);
		if (po == null)
		{
			return false;
		}
		if (!po.equals(prereq.getOperator()))
		{
			writer.write('!');
		}

		writer.write("PRE" + kindHandled().toUpperCase() + ':'
				+ (prereq.isOverrideQualify() ? "Q:" : ""));
		writer.write(po.equals(PrerequisiteOperator.GTEQ) ? prereq.getOperand()
				: "1");
		for (Prerequisite p : prereq.getPrerequisites())
		{
			writer.write(',');
			writer.write(p.getKey());
			writer.write('=');
			writer.write(p.getOperand());
		}
		return true;
	}
}
