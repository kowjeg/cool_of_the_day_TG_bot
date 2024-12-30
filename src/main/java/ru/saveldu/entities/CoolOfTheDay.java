package ru.saveldu.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "cool_of_the_day")
public class CoolOfTheDay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @NotNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Size(max = 255)
    @NotNull
    @Column(name = "user_name", nullable = false)
    private String userName;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDate date;

    public CoolOfTheDay() {
    }

    public Integer getId() {
        return this.id;
    }

    public @NotNull Long getChatId() {
        return this.chatId;
    }

    public @NotNull Long getUserId() {
        return this.userId;
    }

    public @Size(max = 255) @NotNull String getUserName() {
        return this.userName;
    }

    public @NotNull LocalDate getDate() {
        return this.date;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setChatId(@NotNull Long chatId) {
        this.chatId = chatId;
    }

    public void setUserId(@NotNull Long userId) {
        this.userId = userId;
    }

    public void setUserName(@Size(max = 255) @NotNull String userName) {
        this.userName = userName;
    }

    public void setDate(@NotNull LocalDate date) {
        this.date = date;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof CoolOfTheDay)) return false;
        final CoolOfTheDay other = (CoolOfTheDay) o;
        if (!other.canEqual((Object) this)) return false;
        final Object this$id = this.getId();
        final Object other$id = other.getId();
        if (this$id == null ? other$id != null : !this$id.equals(other$id)) return false;
        final Object this$chatId = this.getChatId();
        final Object other$chatId = other.getChatId();
        if (this$chatId == null ? other$chatId != null : !this$chatId.equals(other$chatId)) return false;
        final Object this$userId = this.getUserId();
        final Object other$userId = other.getUserId();
        if (this$userId == null ? other$userId != null : !this$userId.equals(other$userId)) return false;
        final Object this$userName = this.getUserName();
        final Object other$userName = other.getUserName();
        if (this$userName == null ? other$userName != null : !this$userName.equals(other$userName)) return false;
        final Object this$date = this.getDate();
        final Object other$date = other.getDate();
        if (this$date == null ? other$date != null : !this$date.equals(other$date)) return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof CoolOfTheDay;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $id = this.getId();
        result = result * PRIME + ($id == null ? 43 : $id.hashCode());
        final Object $chatId = this.getChatId();
        result = result * PRIME + ($chatId == null ? 43 : $chatId.hashCode());
        final Object $userId = this.getUserId();
        result = result * PRIME + ($userId == null ? 43 : $userId.hashCode());
        final Object $userName = this.getUserName();
        result = result * PRIME + ($userName == null ? 43 : $userName.hashCode());
        final Object $date = this.getDate();
        result = result * PRIME + ($date == null ? 43 : $date.hashCode());
        return result;
    }

    public String toString() {
        return "CoolOfTheDay(id=" + this.getId() + ", chatId=" + this.getChatId() + ", userId=" + this.getUserId() + ", userName=" + this.getUserName() + ", date=" + this.getDate() + ")";
    }
}