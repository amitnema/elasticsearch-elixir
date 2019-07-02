package org.apn.spring.data.es.entities;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

import java.lang.Long;

import static org.springframework.data.elasticsearch.annotations.FieldType.Long;
import static org.springframework.data.elasticsearch.annotations.FieldType.*;

/**
 * @author amit.nema
 *
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
@Document(indexName = "books", type = "_doc")
public class Book {
	@Id
	private String isbn;

	@NonNull
	@MultiField(mainField = @Field(type = Text, fielddata = true), otherFields = {
			@InnerField(suffix = "verbatim", type = Keyword) })
	private String bookTitle;

	@Field(type = Text)
	private String bookAuthor;

	@Field(type = Long)
	private Long yearOfPublication;

	@Field(type = Keyword)
	private String publisher;

	@Field(type = Text)
	private String imageUrlS;

	@Field(type = Text)
	private String imageUrlM;

	@Field(type = Text)
	private String imageUrlL;
}
