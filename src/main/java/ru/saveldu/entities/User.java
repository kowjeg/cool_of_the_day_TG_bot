package ru.saveldu.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;


@Entity
@Table(name = "users")
public class User {
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

    @Column(name = "comb_size")
    private Integer combSize;

    @Column(name = "last_played_date")
    private LocalDate lastPlayedDate;

    public User() {
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

    public Integer getCombSize() {
        return this.combSize;
    }

    public LocalDate getLastPlayedDate() {
        return this.lastPlayedDate;
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

    public void setCombSize(Integer combSize) {
        this.combSize = combSize;
    }

    public void setLastPlayedDate(LocalDate lastPlayedDate) {
        this.lastPlayedDate = lastPlayedDate;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof User)) return false;
        final User other = (User) o;
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
        final Object this$combSize = this.getCombSize();
        final Object other$combSize = other.getCombSize();
        if (this$combSize == null ? other$combSize != null : !this$combSize.equals(other$combSize)) return false;
        final Object this$lastPlayedDate = this.getLastPlayedDate();
        final Object other$lastPlayedDate = other.getLastPlayedDate();
        if (this$lastPlayedDate == null ? other$lastPlayedDate != null : !this$lastPlayedDate.equals(other$lastPlayedDate))
            return false;
        return true;
    }

    protected boolean canEqual(final Object other) {
        return other instanceof User;
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
        final Object $combSize = this.getCombSize();
        result = result * PRIME + ($combSize == null ? 43 : $combSize.hashCode());
        final Object $lastPlayedDate = this.getLastPlayedDate();
        result = result * PRIME + ($lastPlayedDate == null ? 43 : $lastPlayedDate.hashCode());
        return result;
    }

    public String toString() {
        return "User(id=" + this.getId() + ", chatId=" + this.getChatId() + ", userId=" + this.getUserId() + ", userName=" + this.getUserName() + ", combSize=" + this.getCombSize() + ", lastPlayedDate=" + this.getLastPlayedDate() + ")";
    }
}