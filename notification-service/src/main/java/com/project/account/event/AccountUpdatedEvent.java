/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.project.account.event;

import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.util.Utf8;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.SchemaStore;

@org.apache.avro.specific.AvroGenerated
public class AccountUpdatedEvent extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  private static final long serialVersionUID = -4682331298762756731L;


  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"AccountUpdatedEvent\",\"namespace\":\"com.project.account.event\",\"fields\":[{\"name\":\"accountId\",\"type\":\"string\"},{\"name\":\"userEmail\",\"type\":\"string\"}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }

  private static final SpecificData MODEL$ = new SpecificData();

  private static final BinaryMessageEncoder<AccountUpdatedEvent> ENCODER =
      new BinaryMessageEncoder<AccountUpdatedEvent>(MODEL$, SCHEMA$);

  private static final BinaryMessageDecoder<AccountUpdatedEvent> DECODER =
      new BinaryMessageDecoder<AccountUpdatedEvent>(MODEL$, SCHEMA$);

  /**
   * Return the BinaryMessageEncoder instance used by this class.
   * @return the message encoder used by this class
   */
  public static BinaryMessageEncoder<AccountUpdatedEvent> getEncoder() {
    return ENCODER;
  }

  /**
   * Return the BinaryMessageDecoder instance used by this class.
   * @return the message decoder used by this class
   */
  public static BinaryMessageDecoder<AccountUpdatedEvent> getDecoder() {
    return DECODER;
  }

  /**
   * Create a new BinaryMessageDecoder instance for this class that uses the specified {@link SchemaStore}.
   * @param resolver a {@link SchemaStore} used to find schemas by fingerprint
   * @return a BinaryMessageDecoder instance for this class backed by the given SchemaStore
   */
  public static BinaryMessageDecoder<AccountUpdatedEvent> createDecoder(SchemaStore resolver) {
    return new BinaryMessageDecoder<AccountUpdatedEvent>(MODEL$, SCHEMA$, resolver);
  }

  /**
   * Serializes this AccountUpdatedEvent to a ByteBuffer.
   * @return a buffer holding the serialized data for this instance
   * @throws java.io.IOException if this instance could not be serialized
   */
  public java.nio.ByteBuffer toByteBuffer() throws java.io.IOException {
    return ENCODER.encode(this);
  }

  /**
   * Deserializes a AccountUpdatedEvent from a ByteBuffer.
   * @param b a byte buffer holding serialized data for an instance of this class
   * @return a AccountUpdatedEvent instance decoded from the given buffer
   * @throws java.io.IOException if the given bytes could not be deserialized into an instance of this class
   */
  public static AccountUpdatedEvent fromByteBuffer(
      java.nio.ByteBuffer b) throws java.io.IOException {
    return DECODER.decode(b);
  }

  private java.lang.CharSequence accountId;
  private java.lang.CharSequence userEmail;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>.
   */
  public AccountUpdatedEvent() {}

  /**
   * All-args constructor.
   * @param accountId The new value for accountId
   * @param userEmail The new value for userEmail
   */
  public AccountUpdatedEvent(java.lang.CharSequence accountId, java.lang.CharSequence userEmail) {
    this.accountId = accountId;
    this.userEmail = userEmail;
  }

  public org.apache.avro.specific.SpecificData getSpecificData() { return MODEL$; }
  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call.
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return accountId;
    case 1: return userEmail;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  // Used by DatumReader.  Applications should not call.
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: accountId = (java.lang.CharSequence)value$; break;
    case 1: userEmail = (java.lang.CharSequence)value$; break;
    default: throw new IndexOutOfBoundsException("Invalid index: " + field$);
    }
  }

  /**
   * Gets the value of the 'accountId' field.
   * @return The value of the 'accountId' field.
   */
  public java.lang.CharSequence getAccountId() {
    return accountId;
  }


  /**
   * Sets the value of the 'accountId' field.
   * @param value the value to set.
   */
  public void setAccountId(java.lang.CharSequence value) {
    this.accountId = value;
  }

  /**
   * Gets the value of the 'userEmail' field.
   * @return The value of the 'userEmail' field.
   */
  public java.lang.CharSequence getUserEmail() {
    return userEmail;
  }


  /**
   * Sets the value of the 'userEmail' field.
   * @param value the value to set.
   */
  public void setUserEmail(java.lang.CharSequence value) {
    this.userEmail = value;
  }

  /**
   * Creates a new AccountUpdatedEvent RecordBuilder.
   * @return A new AccountUpdatedEvent RecordBuilder
   */
  public static com.project.account.event.AccountUpdatedEvent.Builder newBuilder() {
    return new com.project.account.event.AccountUpdatedEvent.Builder();
  }

  /**
   * Creates a new AccountUpdatedEvent RecordBuilder by copying an existing Builder.
   * @param other The existing builder to copy.
   * @return A new AccountUpdatedEvent RecordBuilder
   */
  public static com.project.account.event.AccountUpdatedEvent.Builder newBuilder(com.project.account.event.AccountUpdatedEvent.Builder other) {
    if (other == null) {
      return new com.project.account.event.AccountUpdatedEvent.Builder();
    } else {
      return new com.project.account.event.AccountUpdatedEvent.Builder(other);
    }
  }

  /**
   * Creates a new AccountUpdatedEvent RecordBuilder by copying an existing AccountUpdatedEvent instance.
   * @param other The existing instance to copy.
   * @return A new AccountUpdatedEvent RecordBuilder
   */
  public static com.project.account.event.AccountUpdatedEvent.Builder newBuilder(com.project.account.event.AccountUpdatedEvent other) {
    if (other == null) {
      return new com.project.account.event.AccountUpdatedEvent.Builder();
    } else {
      return new com.project.account.event.AccountUpdatedEvent.Builder(other);
    }
  }

  /**
   * RecordBuilder for AccountUpdatedEvent instances.
   */
  @org.apache.avro.specific.AvroGenerated
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<AccountUpdatedEvent>
    implements org.apache.avro.data.RecordBuilder<AccountUpdatedEvent> {

    private java.lang.CharSequence accountId;
    private java.lang.CharSequence userEmail;

    /** Creates a new Builder */
    private Builder() {
      super(SCHEMA$, MODEL$);
    }

    /**
     * Creates a Builder by copying an existing Builder.
     * @param other The existing Builder to copy.
     */
    private Builder(com.project.account.event.AccountUpdatedEvent.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.accountId)) {
        this.accountId = data().deepCopy(fields()[0].schema(), other.accountId);
        fieldSetFlags()[0] = other.fieldSetFlags()[0];
      }
      if (isValidValue(fields()[1], other.userEmail)) {
        this.userEmail = data().deepCopy(fields()[1].schema(), other.userEmail);
        fieldSetFlags()[1] = other.fieldSetFlags()[1];
      }
    }

    /**
     * Creates a Builder by copying an existing AccountUpdatedEvent instance
     * @param other The existing instance to copy.
     */
    private Builder(com.project.account.event.AccountUpdatedEvent other) {
      super(SCHEMA$, MODEL$);
      if (isValidValue(fields()[0], other.accountId)) {
        this.accountId = data().deepCopy(fields()[0].schema(), other.accountId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.userEmail)) {
        this.userEmail = data().deepCopy(fields()[1].schema(), other.userEmail);
        fieldSetFlags()[1] = true;
      }
    }

    /**
      * Gets the value of the 'accountId' field.
      * @return The value.
      */
    public java.lang.CharSequence getAccountId() {
      return accountId;
    }


    /**
      * Sets the value of the 'accountId' field.
      * @param value The value of 'accountId'.
      * @return This builder.
      */
    public com.project.account.event.AccountUpdatedEvent.Builder setAccountId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.accountId = value;
      fieldSetFlags()[0] = true;
      return this;
    }

    /**
      * Checks whether the 'accountId' field has been set.
      * @return True if the 'accountId' field has been set, false otherwise.
      */
    public boolean hasAccountId() {
      return fieldSetFlags()[0];
    }


    /**
      * Clears the value of the 'accountId' field.
      * @return This builder.
      */
    public com.project.account.event.AccountUpdatedEvent.Builder clearAccountId() {
      accountId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /**
      * Gets the value of the 'userEmail' field.
      * @return The value.
      */
    public java.lang.CharSequence getUserEmail() {
      return userEmail;
    }


    /**
      * Sets the value of the 'userEmail' field.
      * @param value The value of 'userEmail'.
      * @return This builder.
      */
    public com.project.account.event.AccountUpdatedEvent.Builder setUserEmail(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.userEmail = value;
      fieldSetFlags()[1] = true;
      return this;
    }

    /**
      * Checks whether the 'userEmail' field has been set.
      * @return True if the 'userEmail' field has been set, false otherwise.
      */
    public boolean hasUserEmail() {
      return fieldSetFlags()[1];
    }


    /**
      * Clears the value of the 'userEmail' field.
      * @return This builder.
      */
    public com.project.account.event.AccountUpdatedEvent.Builder clearUserEmail() {
      userEmail = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public AccountUpdatedEvent build() {
      try {
        AccountUpdatedEvent record = new AccountUpdatedEvent();
        record.accountId = fieldSetFlags()[0] ? this.accountId : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.userEmail = fieldSetFlags()[1] ? this.userEmail : (java.lang.CharSequence) defaultValue(fields()[1]);
        return record;
      } catch (org.apache.avro.AvroMissingFieldException e) {
        throw e;
      } catch (java.lang.Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumWriter<AccountUpdatedEvent>
    WRITER$ = (org.apache.avro.io.DatumWriter<AccountUpdatedEvent>)MODEL$.createDatumWriter(SCHEMA$);

  @Override public void writeExternal(java.io.ObjectOutput out)
    throws java.io.IOException {
    WRITER$.write(this, SpecificData.getEncoder(out));
  }

  @SuppressWarnings("unchecked")
  private static final org.apache.avro.io.DatumReader<AccountUpdatedEvent>
    READER$ = (org.apache.avro.io.DatumReader<AccountUpdatedEvent>)MODEL$.createDatumReader(SCHEMA$);

  @Override public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException {
    READER$.read(this, SpecificData.getDecoder(in));
  }

  @Override protected boolean hasCustomCoders() { return true; }

  @Override public void customEncode(org.apache.avro.io.Encoder out)
    throws java.io.IOException
  {
    out.writeString(this.accountId);

    out.writeString(this.userEmail);

  }

  @Override public void customDecode(org.apache.avro.io.ResolvingDecoder in)
    throws java.io.IOException
  {
    org.apache.avro.Schema.Field[] fieldOrder = in.readFieldOrderIfDiff();
    if (fieldOrder == null) {
      this.accountId = in.readString(this.accountId instanceof Utf8 ? (Utf8)this.accountId : null);

      this.userEmail = in.readString(this.userEmail instanceof Utf8 ? (Utf8)this.userEmail : null);

    } else {
      for (int i = 0; i < 2; i++) {
        switch (fieldOrder[i].pos()) {
        case 0:
          this.accountId = in.readString(this.accountId instanceof Utf8 ? (Utf8)this.accountId : null);
          break;

        case 1:
          this.userEmail = in.readString(this.userEmail instanceof Utf8 ? (Utf8)this.userEmail : null);
          break;

        default:
          throw new java.io.IOException("Corrupt ResolvingDecoder.");
        }
      }
    }
  }
}









